package com.acme.application.shared.code;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;

public abstract class BaseCodeType extends AbstractCodeType<String, String> implements IApplicationCodeType {

	private static final long serialVersionUID = 1L;

	@Override
	public String getText() {
		return TEXTS.get(getId());
	}

	@Override
	protected List<? extends ICodeRow<String>> execLoadCodes(Class<? extends ICodeRow<String>> codeRowType) throws ProcessingException {
		return BEANS.get(IApplicationCodeService.class).loadCodeRowsFromDatabase(getId());
	}

	@Override
	public void store(ICodeRow<String> code) {
		if(!isDynamic()) {
			throw new ProcessingException("Code type {} does not allow dynamic managing of codes.", getCodeTypeClass().getSimpleName());
		}
		
		BEANS.get(IApplicationCodeService.class).store(getId(), code);
	}
}
