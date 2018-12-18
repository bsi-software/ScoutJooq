#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.admin;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.text.TEXTS;

import ${package}.client.code.ApplicationCodeTablePage;
import ${package}.shared.code.FileCodeType;
import ${package}.shared.code.LocaleCodeType;
import ${package}.shared.code.SexCodeType;

public class CodeNodePage extends AbstractPageWithNodes {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("CodeNodePage");
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		pageList.add(new ApplicationCodeTablePage(LocaleCodeType.class));
		pageList.add(new ApplicationCodeTablePage(FileCodeType.class));
		pageList.add(new ApplicationCodeTablePage(SexCodeType.class));
	}
}
