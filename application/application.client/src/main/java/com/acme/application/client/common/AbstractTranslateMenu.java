package com.acme.application.client.common;

import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;

import com.acme.application.client.text.TextForm;
import com.acme.application.shared.FontAwesomeIcons;

public abstract class AbstractTranslateMenu extends AbstractMenu {
	
	protected abstract String getObjectId();
	protected abstract void reloadTablePage();
	
	@Override
	protected String getConfiguredText() {
		return TEXTS.get("Translate");
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.fa_language;
	}

	@Override
	protected String getConfiguredKeyStroke() {
		return "alt-t";
	}

	@Override
	protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		return CollectionUtility.hashSet(TableMenuType.SingleSelection);
	}

	@Override
	protected void execAction() {
		TextForm form = new TextForm();
		form.setKey(getObjectId());
		form.startModify();
		form.waitFor();

		if (form.isFormStored()) {
			reloadTablePage();
		}
	}
}
