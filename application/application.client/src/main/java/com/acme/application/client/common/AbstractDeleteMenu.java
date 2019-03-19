package com.acme.application.client.common;

import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService;

import com.acme.application.client.ClientSession;
import com.acme.application.shared.FontAwesomeIcons;
import com.acme.application.shared.common.DeleteEntityPermission;

public abstract class AbstractDeleteMenu extends AbstractMenu {

	@Override
	protected String getConfiguredText() {
		return TEXTS.get("Delete");
	}

	@Override
	protected boolean getConfiguredEnabled() {
		return isOwner() || BEANS.get(IAccessControlService.class).checkPermission(new DeleteEntityPermission());
	}

	private boolean isOwner() {
		return getOwner().getSelectedValues()
				.stream()
				.allMatch(userId -> ClientSession.get().getUserId().equals(userId));
	}

	public abstract AbstractSmartColumn<String> getOwner();

	@Override
	protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.fa_trash;
	}

}
