package com.acme.application.client.common;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;

public class AbstractIdColumn extends AbstractStringColumn {

	@Override
	protected boolean getConfiguredPrimaryKey() {
		return true;
	}
	
	@Override
	protected boolean getConfiguredDisplayable() {
		return false;
	}
}
