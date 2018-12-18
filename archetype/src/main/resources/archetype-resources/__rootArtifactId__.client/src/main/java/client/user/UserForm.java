#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.user;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;

import ${package}.client.common.AbstractDirtyFormHandler;
import ${package}.client.user.UserForm.MainBox.AccountLockedField;
import ${package}.client.user.UserForm.MainBox.CancelButton;
import ${package}.client.user.UserForm.MainBox.OkButton;
import ${package}.client.user.UserForm.MainBox.PasswordField;
import ${package}.client.user.UserForm.MainBox.RoleTableField;
import ${package}.client.user.UserForm.MainBox.UserBox;
import ${package}.shared.user.CreateUserPermission;
import ${package}.shared.user.IUserService;
import ${package}.shared.user.UpdateUserPermission;
import ${package}.shared.user.UserFormData;

@FormData(value = UserFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class UserForm extends AbstractForm {

	public String getUserId() {
		return getUserBox().getUserIdField().getValue();
	}

	public void setUserId(String userId) {
		getUserBox().getUserIdField().setValue(userId);
	}

	@Override
	public Object computeExclusiveKey() {
		return getUserId();
	}

	protected String calculateSubTitle() {
		return getUserId();
	}

	@Override
	protected int getConfiguredDisplayHint() {
		return IForm.DISPLAY_HINT_VIEW;
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("User");
	}

	public void startModify() {
		startInternalExclusive(new ModifyHandler());
	}

	public void startNew() {
		startInternal(new NewHandler());
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	public UserBox getUserBox() {
		return getFieldByClass(UserBox.class);
	}

	public PasswordField getPasswordField() {
		return getFieldByClass(PasswordField.class);
	}

	public RoleTableField getRoleTableField() {
		return getFieldByClass(RoleTableField.class);
	}

	public AccountLockedField getAccountLockedField() {
		return getFieldByClass(AccountLockedField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(10)
	public class MainBox extends AbstractGroupBox {

		@Order(10)
		public class UserBox extends AbstractUserBox {
		}

		@Order(20)
		public class PasswordField extends AbstractPasswordField {

			@Override
			protected void execChangedValue() {
				validateField();
			}
		}

		@Order(25)
		public class AccountLockedField extends AbstractBooleanField {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("AccountIsLocked");
			}
		}

		@Order(30)
		public class RoleTableField extends AbstractTableField<RoleTableField.Table> {

			@Override
			protected boolean getConfiguredLabelVisible() {
				return false;
			}

			@Override
			protected int getConfiguredGridW() {
				return 2;
			}

			@Override
			protected int getConfiguredGridH() {
				return 4;
			}

			public class Table extends AbstractTable {

				public AssignedColumn getAssignedColumn() {
					return getColumnSet().getColumnByClass(AssignedColumn.class);
				}

				public RoleColumn getRoleColumn() {
					return getColumnSet().getColumnByClass(RoleColumn.class);
				}

				public IdColumn getIdColumn() {
					return getColumnSet().getColumnByClass(IdColumn.class);
				}

				@Order(10)
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

				@Order(20)
				public class RoleColumn extends AbstractStringColumn {
					@Override
					protected String getConfiguredHeaderText() {
						return TEXTS.get("Role");
					}

					@Override
					protected int getConfiguredWidth() {
						return 200;
					}
				}

				@Order(30)
				public class AssignedColumn extends AbstractBooleanColumn {
					@Override
					protected String getConfiguredHeaderText() {
						return TEXTS.get("Assigned");
					}

					@Override
					protected int getConfiguredWidth() {
						return 50;
					}

					@Override
					protected boolean getConfiguredEditable() {
						return true;
					}
				}
			}
		}

		@Order(100000)
		public class OkButton extends AbstractOkButton {
		}

		@Order(101000)
		public class CancelButton extends AbstractCancelButton {
		}
	}

	public class ModifyHandler extends AbstractDirtyFormHandler {

		@Override
		protected void execLoad() {
			setEnabledPermission(new UpdateUserPermission());

			UserFormData formData = new UserFormData();
			exportFormData(formData);
			formData = BEANS.get(IUserService.class).load(formData);
			importFormData(formData);

			getForm().setSubTitle(calculateSubTitle());
		}

		@Override
		protected void execStore() {
			UserFormData formData = new UserFormData();
			exportFormData(formData);
			formData = BEANS.get(IUserService.class).store(formData);
		}

		@Override
		protected void execDirtyStatusChanged(boolean dirty) {
			getForm().setSubTitle(calculateSubTitle());
		}

		@Override
		protected boolean getConfiguredOpenExclusive() {
			return true;
		}
	}

	public class NewHandler extends AbstractDirtyFormHandler {

		@Override
		protected void execLoad() {
			setEnabledPermission(new CreateUserPermission());
			
			getPasswordField().setMandatory(true);
			UserFormData formData = new UserFormData();
			exportFormData(formData);
			formData = BEANS.get(IUserService.class).load(formData);
			importFormData(formData);

			getForm().setSubTitle(calculateSubTitle());
		}

		@Override
		protected void execStore() {
			IUserService service = BEANS.get(IUserService.class);

			String userId = getUserId();
			if (BEANS.get(IUserService.class).exists(userId)) {
				throw new VetoException(TEXTS.get("AccountAlreadyExists", userId));
			}

			UserFormData formData = new UserFormData();
			exportFormData(formData);
			formData = service.store(formData);
			importFormData(formData);
		}

		@Override
		protected void execDirtyStatusChanged(boolean dirty) {
			getForm().setSubTitle(calculateSubTitle());
		}
	}
}
