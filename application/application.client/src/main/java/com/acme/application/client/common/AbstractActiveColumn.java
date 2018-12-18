package com.acme.application.client.common;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractActiveColumn extends AbstractBooleanColumn {
	@Override
	protected String getConfiguredHeaderText() {
		return TEXTS.get("Active");
	}

	@Override
	protected int getConfiguredWidth() {
		return 100;
	}
}
