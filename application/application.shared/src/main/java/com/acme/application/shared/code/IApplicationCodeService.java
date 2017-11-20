package com.acme.application.shared.code;

import java.util.List;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;

@TunnelToServer
public interface IApplicationCodeService extends IService {

	ApplicationCodePageData getApplicationCodeTableData(Class<? extends IApplicationCodeType> codeTypeClass);

	ApplicationCodeFormData load(ApplicationCodeFormData formData);

	ApplicationCodeFormData store(ApplicationCodeFormData formData);

	/**
	 * (Re)loads dynamic codes by loading codes from the database.
	 * @return 
	 */
	List<ICodeRow<String>> loadCodeRowsFromDatabase(String codeTypeId);

	void store(String codeTypeId, ICodeRow<String> code);
}
