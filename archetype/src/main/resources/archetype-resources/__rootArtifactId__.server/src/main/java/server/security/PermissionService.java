#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.security;

import java.security.BasicPermission;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.inventory.ClassInventory;
import org.eclipse.scout.rt.platform.inventory.IClassInfo;
import org.eclipse.scout.rt.platform.inventory.IClassInventory;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.database.or.core.tables.records.TextRecord;
import ${package}.server.text.TextService;

/**
 * Class copied / adapted from scout.rt.server package.
 */
@Order(1000)
public class PermissionService implements IPermissionService {

	// TODO move this list to config.properties
	// list of Scout permissions that are irrelevant for this application
	public static final String [] EXCLUDE_ARRAY = new String [] {
			"org.eclipse.scout.rt.shared.security.CreateGlobalBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.ReadGlobalBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.UpdateGlobalBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.DeleteGlobalBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.CreateUserBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.ReadUserBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.UpdateUserBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.DeleteUserBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.PublishUserBookmarkPermission",
			"org.eclipse.scout.rt.shared.security.CreateCustomColumnPermission",
			"org.eclipse.scout.rt.shared.security.UpdateCustomColumnPermission",
			"org.eclipse.scout.rt.shared.security.DeleteCustomColumnPermission",
			"org.eclipse.scout.rt.shared.security.ReadDiagnosticServletPermission",
			"org.eclipse.scout.rt.shared.security.UpdateDiagnosticServletPermission",
			"org.eclipse.scout.rt.shared.security.RemoteServiceAccessPermission",
			"org.eclipse.scout.rt.shared.security.UpdateServiceConfigurationPermission"
	};

	private static final Logger LOG = LoggerFactory.getLogger(PermissionService.class);

	private final Object permissionClassesLock = new Object();
	private Set<Class<? extends Permission>> permissionClasses;
	private Map<String, Permission> permissionMap = new HashMap<>();

	private void checkTranslations(Class<? extends Permission> permission) {
		String id = permission.getName();
		String prefix = permission.getPackage().getName();
		String group = prefix.substring(prefix.lastIndexOf(".") + 1);
		String key = permission.getSimpleName();

		checkTranslation(prefix, group);
		checkTranslation(id, key);
	}

	private void checkTranslation(String key, String text) {
		if (StringUtility.hasText(key)) {
			List<TextRecord> texts = BEANS.get(TextService.class).getAll(key);

			if(texts.size() == 0) {
				String t1 = StringUtility.splitCamelCase(text);
				String t2 = t1.substring(0, 1).toUpperCase() + t1.substring(1);

				if (t2.endsWith(" Permission")) {
					t2 = t2.substring(0, t2.indexOf(" Permission"));
				}

				String locale = Locale.ROOT.toLanguageTag();
				String id = TextService.toId(locale, key);
				BEANS.get(TextService.class).store(id, new TextRecord(key, locale, t2));
			}
		}
	}

	@Override
	public Set<Class<? extends Permission>> getAllPermissionClasses() {
		checkCache();
		return CollectionUtility.hashSet(permissionClasses);
	}

	public List<String> getPermissionKeys() {
		checkCache();
		return new ArrayList<String>(permissionMap.keySet());
	}

	/**
	 * Gets permission from cache via it's class name (i.e. permissionclass.getName()).
	 *
	 * @param key
	 *          the fully classified class name of the permission
	 * @return the permission class or null if no such permission exists in the cache
	 */
	public Permission getPermission(String key) {
		return permissionMap.get(key);
	}

	private void checkCache() {
		synchronized (permissionClassesLock) {
			// null-check with lock (valid check)
			if(permissionClasses == null) {
				Set<IClassInfo> allKnownPermissions = getPermissionsFromInventory();
				Set<String> excludePermissions = getPermissionsToExclude();
				Set<Class<? extends Permission>> discoveredPermissions;

				discoveredPermissions = processPermission(allKnownPermissions, excludePermissions);
				permissionClasses = CollectionUtility.hashSet(discoveredPermissions);
			}
		}
	}
	
	public void checkTranslations() {
		getAllPermissionClasses()
		.stream()
		.forEach(permission -> {
			checkTranslations(permission);
		});
	}

	private Set<Class<? extends Permission>> processPermission(Set<IClassInfo> allPermissions, Set<String> excludePermissions) {
		Set<Class<? extends Permission>> discoveredPermissions = new HashSet<>(allPermissions.size());

		for (IClassInfo permInfo : allPermissions) {
			if (acceptClass(permInfo)) {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Permission> permClass = (Class<? extends Permission>) permInfo.resolveClass();

					String name = permInfo.name();
					if (!excludePermissions.contains(name)) {
						discoveredPermissions.add(permClass);

						Permission permission = (Permission) Class.forName(permClass.getName()).newInstance();
						permissionMap.put(name, permission);
					}
				}
				catch (Exception e) {
					LOG.warn("Unable to load permission: " + e.getLocalizedMessage());
				}
			}
		}

		return discoveredPermissions;
	}

	/**
	 * @return Permission classes from class inventory. By default all direct subclasses of {@link Permission} and
	 *         {@link BasicPermission} that are available in the {@link ClassInventory} are returned.
	 */
	protected Set<IClassInfo> getPermissionsFromInventory() {
		IClassInventory inv = ClassInventory.get();
		//get BasicPermssion subclasses are not found directly, because jdk is not scanned by jandex.
		Set<IClassInfo> classes = inv.getAllKnownSubClasses(Permission.class);
		classes.addAll(inv.getAllKnownSubClasses(BasicPermission.class));
		return classes;
	}

	/**
	 * Checks whether the given class is a Permission class that should be visible to this service. The default
	 * implementation checks if the class meets the following conditions:
	 * <ul>
	 * <li>class is instanciable (public, not abstract, not interface, not inner member type)
	 * <li>the name is accepted by {@link ${symbol_pound}acceptClassName(String)}
	 * </ul>
	 *
	 * @param permInfo
	 *          the class to be checked
	 * @return Returns <code>true</code> if the class used by this service. <code>false</code> otherwise.
	 */
	protected boolean acceptClass(IClassInfo permInfo) {
		return permInfo.isInstanciable() && acceptClassName(permInfo.name());
	}

	/**
	 * Checks whether the given class name is a potential permission class and used by this service.
	 *
	 * @param className
	 *          the class name to be checked
	 * @return Returns <code>true</code> by default.
	 */
	protected boolean acceptClassName(String className) {
		return true;
	}

	/**
	 * Returns the set of irrelevant (for this application) permissions.
	 */
	private Set<String> getPermissionsToExclude() {
		Set<String> excludeList = new HashSet<String>();
		Arrays.asList(EXCLUDE_ARRAY)
		.stream()
		.forEach(permission -> {
			if (StringUtility.hasText(permission)) {
				excludeList.add(permission);
			}
		});

		return excludeList;
	}
}
