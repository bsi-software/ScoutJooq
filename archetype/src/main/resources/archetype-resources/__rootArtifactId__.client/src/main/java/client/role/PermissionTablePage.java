#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.role;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import ${package}.client.role.PermissionTablePage.PermissionTable;
import ${package}.shared.role.IRoleService;
import ${package}.shared.role.PermissionTablePageData;
import ${package}.shared.role.ReadPermissionPagePermission;

@Data(PermissionTablePageData.class)
public class PermissionTablePage extends AbstractPageWithTable<PermissionTable> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("PermissionTablePage");
	}

	@Override
	protected void execInitPage() {
		setVisiblePermission(new ReadPermissionPagePermission());
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	public class PermissionTable extends AbstractPermissionTable {
		
		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		@Override
		protected void execInitTable() {
			getAssignedColumn().setDisplayable(false);
		}

		@Override
		protected void execReloadPage() {
			reloadPage();
		}
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IRoleService.class).getPermissionTableData(filter));
	}
}
