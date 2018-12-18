#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.role;

import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import ${package}.client.common.AbstractExportableTable;
import ${package}.client.text.AbstractTranslateMenu;

public abstract class AbstractPermissionTable extends AbstractExportableTable {
	
	protected abstract void execReloadPage();

	@Override
	protected void execRowAction(ITableRow row) {
		getMenuByClass(TranslateMenu.class).execAction();
	}

	public IdColumn getIdColumn() {
		return getColumnSet().getColumnByClass(IdColumn.class);
	}

	public GroupColumn getGroupColumn() {
		return getColumnSet().getColumnByClass(GroupColumn.class);
	}

	public TextColumn getTextColumn() {
		return getColumnSet().getColumnByClass(TextColumn.class);
	}

	public AssignedColumn getAssignedColumn() {
		return getColumnSet().getColumnByClass(AssignedColumn.class);
	}

	private String getGroupId(String permissionId) {
		return permissionId.substring(0, permissionId.lastIndexOf("."));
	}

	@Order(10)
	public class TranslateMenu extends AbstractTranslateMenu {

		@Override
		protected String getTextKey() {
			return getIdColumn().getSelectedValue();
		}

		@Override
		protected void reload() {
			execReloadPage();
		}
		
		@Override
		public void execAction() {
			super.execAction();
		}
	}

	@Order(20)
	public class TranslateGroupMenu extends AbstractTranslateMenu {

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("TranslateGroup");
		}

		@Override
		protected String getConfiguredKeyStroke() {
			return "alt-g";
		}

		@Override
		protected String getTextKey() {
			return getGroupId(getIdColumn().getSelectedValue());
		}

		@Override
		protected void reload() {
			execReloadPage();
		}
	}

	@Order(1000)
	public class IdColumn extends AbstractStringColumn {

		@Override
		protected boolean getConfiguredPrimaryKey() {
			return true;
		}

		@Override
		protected boolean getConfiguredDisplayable() {
			return false;
		}
	}

	@Order(2000)
	public class GroupColumn extends AbstractStringColumn {
		@Override
		protected String getConfiguredHeaderText() {
			return TEXTS.get("PermissionGroup");
		}

		@Override
		protected int getConfiguredWidth() {
			return 200;
		}
	}

	@Order(3000)
	public class TextColumn extends AbstractStringColumn {
		@Override
		protected String getConfiguredHeaderText() {
			return TEXTS.get("Permission");
		}

		@Override
		protected int getConfiguredWidth() {
			return 250;
		}
	}

	@Order(4000)
	public class AssignedColumn extends AbstractBooleanColumn {
		@Override
		protected String getConfiguredHeaderText() {
			return TEXTS.get("Assigned");
		}

		@Override
		protected int getConfiguredWidth() {
			return 100;
		}

		@Override
		protected boolean getConfiguredEditable() {
			return true;
		}
	}
}
