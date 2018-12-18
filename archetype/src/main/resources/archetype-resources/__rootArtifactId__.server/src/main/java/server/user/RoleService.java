#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.user;

import java.security.Permission;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService;
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.database.or.core.tables.Role;
import ${package}.database.or.core.tables.RolePermission;
import ${package}.database.or.core.tables.records.RolePermissionRecord;
import ${package}.database.or.core.tables.records.RoleRecord;
import ${package}.database.or.core.tables.records.TextRecord;
import ${package}.database.table.RoleTable;
import ${package}.database.table.TextTable;
import ${package}.server.ServerSession;
import ${package}.server.common.AbstractBaseService;
import ${package}.server.security.PermissionService;
import ${package}.server.text.TextService;
import ${package}.shared.role.IRoleService;
import ${package}.shared.role.PermissionTablePageData;
import ${package}.shared.role.RoleFormData;
import ${package}.shared.role.RoleFormData.PermissionTable;
import ${package}.shared.role.RoleFormData.PermissionTable.PermissionTableRowData;
import ${package}.shared.role.RoleTablePageData;
import ${package}.shared.role.RoleTablePageData.RoleTableRowData;

public class RoleService extends AbstractBaseService<Role, RoleRecord> implements IRoleService {

    @Override
    public Role getTable() {
        return Role.ROLE;
    }

    @Override
    public Field<String> getIdColumn() {
        return Role.ROLE.NAME;
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(RoleService.class);
    }

    /**
     * Returns the role object for the specified id. Returns a new empty person
     * record if no such person exists.
     */
    public RoleRecord getOrCreate(String roleName) {
        RoleRecord role = get(roleName);

        if (role != null) {
            return role;
        }

        role = new RoleRecord();
        role.setName(roleName);

        return role;
    }

    /**
     * Returns the set of permissions for the role specified by the provided name.
     */
    public List<Permission> getPermissions(String role) {
        RolePermission rpt = RolePermission.ROLE_PERMISSION;
        PermissionService permissionService = BEANS.get(PermissionService.class);

        return getContext()
                .select(rpt.PERMISSION)
                .from(rpt)
                .where(rpt.ROLE_NAME.eq(role))
                .fetchStream()
                .map(record -> {
                    String permissionId = rpt.field(rpt.PERMISSION).getValue(record);
                    return permissionService.getPermission(permissionId);
                })
                .collect(Collectors.toList());
    }

    /**
     * Persists the provided role, including associated permissions.
     */
    public void store(RoleRecord role, List<String> permissions) {
        getLogger().info("persisting role {}", role);

        store(role.getName(), role);
        storeRolePermissions(role, permissions);

        BEANS.get(IAccessControlService.class).clearCache();
    }

    /**
     * Persists the provided role permission.
     */
    private void storeRolePermissions(RoleRecord role, List<String> permissions) {
        DSLContext context = getContext();

        // delete existing role permissions
        String roleName = role.getName();
        RolePermission rpt = RolePermission.ROLE_PERMISSION;

        context
                .deleteFrom(rpt)
                .where(rpt.ROLE_NAME.eq(roleName))
                .execute();

        // add new user roles
        permissions
                .stream()
                .forEach(permission -> context
                        .executeInsert(new RolePermissionRecord(roleName, permission)));
    }

    @Override
    public AbstractTablePageData getRoleTableData(SearchFilter filter) {
        RoleTablePageData pageData = new RoleTablePageData();
        Locale locale = ServerSession.get().getLocale();

        getAll()
                .stream()
                .forEach(role -> {
                    String roleName = role.getName();
                    String roleTextId = RoleTable.toTextKey(roleName);

                    RoleTableRowData row = pageData.addRow();
                    row.setId(roleName);
                    row.setTextId(roleTextId);
                    row.setName(TEXTS.getWithFallback(locale, roleTextId, roleName));
                });

        return pageData;
    }

    @Override
    public AbstractTablePageData getPermissionTableData(SearchFilter filter) {
        PermissionTablePageData pageData = new PermissionTablePageData();
        Locale locale = ServerSession.get().getLocale();

        BEANS.get(PermissionService.class)
                .getAllPermissionClasses()
                .stream()
                .forEach(permission -> {
                    ${package}.shared.role.PermissionTablePageData.PermissionTableRowData row = pageData
                            .addRow();
                    String id = permission.getName();
                    String group = permission.getPackage().getName();
                    row.setId(id);
                    row.setGroup(TEXTS.getWithFallback(locale, group, group));
                    row.setText(TEXTS.getWithFallback(locale, id, id));
                });

        return pageData;
    }

    @Override
    public RoleFormData load(RoleFormData formData) {
        List<String> permissions = getRolePermissions(formData);
        addPermissionRows(formData, permissions);

        return formData;
    }

    private List<String> getRolePermissions(RoleFormData formData) {
        String role = formData.getRoleId().getValue();

        return getPermissions(role)
                .stream()
                .map(permission -> {
                    return permission.getClass().getName();
                })
                .collect(Collectors.toList());
    }

    private void addPermissionRows(RoleFormData formData, List<String> permissions) {
        String role = formData.getRoleId().getValue();
        boolean isRoot = RoleTable.ROOT.equals(role);

        PermissionTable table = formData.getPermissionTable();
        table.clearRows();

        BEANS.get(IPermissionService.class)
                .getAllPermissionClasses()
                .stream()
                .forEach(permission -> {
                    String id = permission.getName();
                    String groupId = permission.getPackage().getName();
                    String group = TEXTS.getWithFallback(groupId, groupId);
                    String name = TEXTS.getWithFallback(id, id);
                    PermissionTableRowData row = table.addRow();

                    row.setId(id);
                    row.setGroup(group);
                    row.setText(name);
                    row.setAssigned(permissions.contains(id) || isRoot);
                });
    }

    @Override
    public RoleFormData store(RoleFormData formData) {
        String role = formData.getRoleId().getValue();
        PermissionTable table = formData.getPermissionTable();

        // store role record
        String roleTextId = RoleTable.toTextKey(role);
        RoleRecord record = new RoleRecord(role, roleTextId, true);
        List<String> permissions = Arrays.asList(table.getRows())
                .stream()
                .filter(row -> row.getAssigned())
                .map(row -> {
                    return row.getId();
                })
                .collect(Collectors.toList());

        store(record, permissions);

        // store role default text translation
        TextRecord roleText = new TextRecord(roleTextId, TextTable.LOCALE_DEFAULT, role);
        BEANS.get(TextService.class).store(roleText);

        return formData;
    }
}
