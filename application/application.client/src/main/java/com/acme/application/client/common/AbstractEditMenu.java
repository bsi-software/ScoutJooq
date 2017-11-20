package com.acme.application.client.common;

import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;

import com.acme.application.shared.FontAwesomeIcons;

public class AbstractEditMenu extends AbstractMenu {

	@Override
	protected String getConfiguredText() {
		return TEXTS.get("Edit");
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.fa_pencil;
	}

	@Override
	protected String getConfiguredKeyStroke() {
		return "alt-e";
	}

	@Override
	protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		return CollectionUtility.hashSet(TableMenuType.SingleSelection);
	}
}
