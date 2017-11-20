#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.role;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.shared.TEXTS;

import ${package}.client.common.AbstractDirtyFormHandler;
import ${package}.client.role.RoleForm.MainBox.CancelButton;
import ${package}.client.role.RoleForm.MainBox.OkButton;
import ${package}.client.role.RoleForm.MainBox.RoleBox;
import ${package}.client.role.RoleForm.MainBox.RoleBox.PermissionTableField;
import ${package}.client.role.RoleForm.MainBox.RoleBox.RoleIdField;
import ${package}.shared.role.CreateRolePermission;
import ${package}.shared.role.IRoleService;
import ${package}.shared.role.RoleFormData;
import ${package}.shared.role.UpdateRolePermission;

@FormData(value = RoleFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class RoleForm extends AbstractForm {

	public String getRoleId() {
		return getRoleIdField().getValue();
	}

	public void setRoleId(String roleId) {
		getRoleIdField().setValue(roleId);
	}

	public void setRoleIdEnabled(boolean enabled) {
		getRoleIdField().setEnabled(enabled);
		getRoleIdField().setMandatory(enabled);
	}

	@Override
	public Object computeExclusiveKey() {
		return getRoleId();
	}

	protected String calculateSubTitle() {
		return getRoleId();
	}

	@Override
	protected int getConfiguredDisplayHint() {
		return IForm.DISPLAY_HINT_VIEW;
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Role");
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

	public RoleIdField getRoleIdField() {
		return getFieldByClass(RoleIdField.class);
	}

	public PermissionTableField getPermissionTableField() {
		return getFieldByClass(PermissionTableField.class);
	}

	public RoleBox getRoleBox() {
		return getFieldByClass(RoleBox.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {

		@Order(1000)
		public class RoleBox extends AbstractGroupBox {

			@Order(1000)
			public class RoleIdField extends AbstractStringField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("RoleName");
				}

				@Override
				protected boolean getConfiguredMandatory() {
					return true;
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

			@Order(2000)
			public class PermissionTableField extends AbstractTableField<PermissionTableField.Table> {

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

				public class Table extends AbstractPermissionTable {

					@Override
					public IOutline getPageOutline() {
						return null;
					}

					@Override
					protected void execReloadPage() {
						reloadTableData();
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
			setEnabledPermission(new UpdateRolePermission());

			RoleFormData formData = new RoleFormData();
			exportFormData(formData);
			formData = BEANS.get(IRoleService.class).load(formData);
			importFormData(formData);

			getForm().setSubTitle(calculateSubTitle());
		}

		@Override
		protected void execStore() {
			RoleFormData formData = new RoleFormData();
			exportFormData(formData);
			formData = BEANS.get(IRoleService.class).store(formData);
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
			setEnabledPermission(new CreateRolePermission());
			
			RoleFormData formData = new RoleFormData();
			exportFormData(formData);
			formData = BEANS.get(IRoleService.class).load(formData);
			importFormData(formData);

			getForm().setSubTitle(calculateSubTitle());
		}

		@Override
		protected void execStore() {
			IRoleService service = BEANS.get(IRoleService.class);

			String role = getRoleId();
			if (BEANS.get(IRoleService.class).exists(role)) {
				throw new VetoException(TEXTS.get("RoleAlreadyExists", role));
			}

			RoleFormData formData = new RoleFormData();
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
