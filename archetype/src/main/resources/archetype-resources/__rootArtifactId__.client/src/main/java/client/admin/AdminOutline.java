#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.admin;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

import ${package}.client.text.TextTablePage;
import ${package}.shared.FontAwesomeIcons;

@Order(3000)
public class AdminOutline extends AbstractOutline {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Administration");
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.fa_users;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		pageList.add(new AccountNodePage());
		pageList.add(new CodeNodePage());
		pageList.add(new TextTablePage());
	}
}
