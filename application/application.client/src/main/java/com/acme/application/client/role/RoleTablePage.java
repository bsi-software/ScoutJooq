package com.acme.application.client.role;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.acme.application.client.common.AbstractEditMenu;
import com.acme.application.client.common.AbstractExportableTable;
import com.acme.application.client.common.AbstractNewMenu;
import com.acme.application.client.common.AbstractTranslateMenu;
import com.acme.application.client.role.RoleTablePage.Table;
import com.acme.application.shared.role.IRoleService;
import com.acme.application.shared.role.ReadRolePagePermission;
import com.acme.application.shared.role.RoleTablePageData;

@Data(RoleTablePageData.class)
public class RoleTablePage extends AbstractPageWithTable<Table> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("RoleTablePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected void execInitPage() {
		setVisiblePermission(new ReadRolePagePermission());
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IRoleService.class).getRoleTableData(filter));
	}

	public class Table extends AbstractExportableTable {
		
		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		@Override
		protected void execRowAction(ITableRow row) {
			getMenuByClass(EditMenu.class).execAction();
		}

		public TextIdColumn getTextIdColumn() {
			return getColumnSet().getColumnByClass(TextIdColumn.class);
		}

		public NameColumn getNameColumn() {
			return getColumnSet().getColumnByClass(NameColumn.class);
		}

		public IdColumn getIdColumn() {
			return getColumnSet().getColumnByClass(IdColumn.class);
		}

		@Order(10)
		public class NewMenu extends AbstractNewMenu {

			@Override
			protected void execAction() {
				RoleForm form = new RoleForm();
				form.addFormListener(new RoleFormListener());
				form.startNew();
			}
		}

		@Order(20)
		public class EditMenu extends AbstractEditMenu {

			@Override
			protected void execAction() {
				RoleForm form = new RoleForm();
				form.addFormListener(new RoleFormListener());
				form.setRoleId(getRoleId());
				form.setRoleIdEnabled(false);
				form.startModify();
			}
		}

		@Order(30)
		public class TranslateMenu extends AbstractTranslateMenu {

			@Override
			protected String getObjectId() {
				return getTextId();
			}

			@Override
			protected void reloadTablePage() {
				reloadPage();
			}
		}
		
		private String getTextId() {
			return getTextIdColumn().getSelectedValue();
		}
		
		private String getRoleId() {
			return getIdColumn().getSelectedValue();
		}

		private class RoleFormListener implements FormListener {

			@Override
			public void formChanged(FormEvent e) {
				if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
					reloadPage();
				}
			}
		}

		@Order(10)
		public class IdColumn extends AbstractStringColumn {

			@Override
			protected boolean getConfiguredPrimaryKey() {
				return true;
			}

			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("RoleName");
			}

			@Override
			protected int getConfiguredWidth() {
				return 150;
			}
		}

		@Order(15)
		public class TextIdColumn extends AbstractStringColumn {
			
			@Override
			protected boolean getConfiguredDisplayable() {
				return false;
			}
		}
		
		

		@Order(20)
		public class NameColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("RoleText");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}
	}
}
