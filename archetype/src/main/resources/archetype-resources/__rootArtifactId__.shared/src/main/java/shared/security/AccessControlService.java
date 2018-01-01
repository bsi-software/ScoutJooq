#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.security;

import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.cache.ICacheBuilder;
import org.eclipse.scout.rt.shared.security.RemoteServiceAccessPermission;
import org.eclipse.scout.rt.shared.services.common.security.AbstractAccessControlService;
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.shared.user.IUserService;

/**
 * <h3>{@link AccessControlService}</h3> {@link IAccessControlService} service that uses {@link ISession${symbol_pound}getUserId()} as
 * internal cache key required by {@link AbstractAccessControlService} implementation.
 */
public class AccessControlService extends AbstractAccessControlService<String> {
	private static final Logger LOG = LoggerFactory.getLogger(AccessControlService.class);

	@Override
	protected String getCurrentUserCacheKey() {
		return getUserIdOfCurrentUser();
	}

    // TODO check if this is necessary
	@Override
	protected ICacheBuilder<String, PermissionCollection> createCacheBuilder() {
		@SuppressWarnings("unchecked")
		ICacheBuilder<String, PermissionCollection> cacheBuilder = BEANS.get(ICacheBuilder.class);
		return cacheBuilder.withCacheId(ACCESS_CONTROL_SERVICE_CACHE_ID).withValueResolver(createCacheValueResolver())
				.withShared(false)
				.withClusterEnabled(false)
				.withTransactional(false)
				.withTransactionalFastForward(false)
				.withTimeToLive(1L, TimeUnit.HOURS, false);
	}

    // TODO check if this is necessary
	@Override
	public void clearCache() {
		LOG.info("clearing cache");
		super.clearCache();
	}

	@Override
	protected PermissionCollection execLoadPermissions(String userId) {
		LOG.info("loading permissions for user '" + userId + "'");
		
	    Permissions permissions = new Permissions();
	    Set<String> permissionNames = new HashSet<>();
	    
	    // TODO check if this is necessary
	    permissions.add(new RemoteServiceAccessPermission("*.shared.*", "*"));
	    
	    // add permissions specific to this user
	    BEANS.get(IUserService.class)
	    .getPermissions(userId)
	    .stream()
	    .forEach(permission -> {
	    	permissions.add(permission);
	    	permissionNames.add(permission.toString());
	    	});
	    
		LOG.info("permissions list: [" + StringUtility.join(",", permissionNames) + "]");

		return permissions;
	}
}
