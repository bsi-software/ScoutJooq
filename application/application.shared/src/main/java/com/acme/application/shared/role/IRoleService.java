package com.acme.application.shared.role;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface IRoleService extends IService {

	boolean exists(String role);

	RoleFormData load(RoleFormData formData);

	RoleFormData store(RoleFormData formData);

	AbstractTablePageData getRoleTableData(SearchFilter filter);
	
	AbstractTablePageData getPermissionTableData(SearchFilter filter);
}
