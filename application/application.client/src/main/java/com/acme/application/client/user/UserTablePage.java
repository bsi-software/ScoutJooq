package com.acme.application.client.user;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.acme.application.client.common.AbstractExportableTable;
import com.acme.application.client.user.UserTablePage.Table;
import com.acme.application.shared.FontAwesomeIcons;
import com.acme.application.shared.user.CreateUserPermission;
import com.acme.application.shared.user.IUserService;
import com.acme.application.shared.user.UserTablePageData;

@Data(UserTablePageData.class)
public class UserTablePage extends AbstractPageWithTable<Table> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("UserTablePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IUserService.class).getUserTableData(filter));
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

		public IsLockedColumn getIsLockedColumn() {
			return getColumnSet().getColumnByClass(IsLockedColumn.class);
		}

		public UserIdColumn getUserIdColumn() {
			return getColumnSet().getColumnByClass(UserIdColumn.class);
		}

		public FirstNameColumn getFirstNameColumn() {
			return getColumnSet().getColumnByClass(FirstNameColumn.class);
		}

		public IsRootColumn isRootColumn() {
			return getColumnSet().getColumnByClass(IsRootColumn.class);
		}

		public LastNameColumn getLastNameColumn() {
			return getColumnSet().getColumnByClass(LastNameColumn.class);
		}

		@Order(1000)
		public class UserIdColumn extends AbstractStringColumn {

			@Override
			protected boolean getConfiguredPrimaryKey() {
				return true;
			}

			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("UserName");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(2000)
		public class FirstNameColumn extends AbstractStringColumn {

			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("FirstName");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(3000)
		public class LastNameColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("LastName");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(4000)
		public class IsRootColumn extends AbstractBooleanColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("RootColumn");
			}

			@Override
			protected int getConfiguredWidth() {
				return 150;
			}
		}

		@Order(5000)
		public class IsLockedColumn extends AbstractBooleanColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("IsLocked");
			}

			@Override
			protected int getConfiguredWidth() {
				return 120;
			}
		}

		@Order(1000)
		public class NewMenu extends AbstractMenu {

			@Override
			protected void execInitAction() {
				setVisiblePermission(new CreateUserPermission());
			}

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("New");
			}

			@Override
			protected String getConfiguredIconId() {
				return FontAwesomeIcons.fa_magic;
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection, TableMenuType.MultiSelection);
			}

			@Override
			protected void execAction() {
				UserForm form = new UserForm();
				form.addFormListener(new UserFormListener());
				form.getUserBox().setUserIdEnabled(true);
				form.startNew();
			}
		}

		@Order(2000)
		public class EditMenu extends AbstractMenu {

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Edit");
			}

			@Override
			protected String getConfiguredIconId() {
				return FontAwesomeIcons.fa_pencil;
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.SingleSelection);
			}

			@Override
			protected void execAction() {
				UserForm form = new UserForm();
				form.addFormListener(new UserFormListener());
				form.setUserId(getTable().getUserIdColumn().getSelectedValue());
				form.getUserBox().setUserIdEnabled(false);
				form.startModify();
			}
		}

		protected class UserFormListener implements FormListener {

			@Override
			public void formChanged(FormEvent e) {
				if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
					reloadPage();
				}
			}
		}
	}
}
