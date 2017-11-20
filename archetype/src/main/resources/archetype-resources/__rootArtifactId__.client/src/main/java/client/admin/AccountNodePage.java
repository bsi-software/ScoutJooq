#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.admin;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

import ${package}.client.role.PermissionTablePage;
import ${package}.client.role.RoleTablePage;
import ${package}.client.user.UserTablePage;

public class AccountNodePage extends AbstractPageWithNodes {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("AccountNodePage");
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		pageList.add(new UserTablePage());
		pageList.add(new RoleTablePage());
		pageList.add(new PermissionTablePage());
	}
}
