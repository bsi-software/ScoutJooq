package com.acme.application.shared.code;

import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;

public interface IApplicationCodeType extends ICodeType<String, String> {

	/**
	 * Returns the concrete code type class.
	 */
	abstract Class<? extends IApplicationCodeType> getCodeTypeClass();
	
	/**
	 * Returns true iff codes of this code type might be managed dynamically by the application.
	 */
	boolean isDynamic();
	
	/**
	 * Adds/updates the provided code for the dynamic code list of this code type.
	 * Throws a {@ProcessingException} if code type does not allow dynamic management of codes.
	 */
	void store(ICodeRow<String> code);
}
