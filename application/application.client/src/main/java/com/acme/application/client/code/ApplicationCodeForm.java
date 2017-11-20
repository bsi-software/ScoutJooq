package com.acme.application.client.code;

import java.math.BigDecimal;
import java.security.Permission;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.bigdecimalfield.AbstractBigDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

import com.acme.application.client.ClientSession;
import com.acme.application.client.code.ApplicationCodeForm.MainBox.CancelButton;
import com.acme.application.client.code.ApplicationCodeForm.MainBox.CodeBox.CodeIdField;
import com.acme.application.client.code.ApplicationCodeForm.MainBox.CodeBox.CodeTextField;
import com.acme.application.client.code.ApplicationCodeForm.MainBox.CodeBox.OrderField;
import com.acme.application.client.code.ApplicationCodeForm.MainBox.OkButton;
import com.acme.application.client.common.AbstractDirtyFormHandler;
import com.acme.application.client.role.RoleForm.MainBox.RoleBox;
import com.acme.application.client.role.RoleForm.MainBox.RoleBox.PermissionTableField;
import com.acme.application.shared.code.ApplicationCodeFormData;
import com.acme.application.shared.code.ApplicationCodeUtility;
import com.acme.application.shared.code.CreateApplicationCodePermission;
import com.acme.application.shared.code.IApplicationCodeService;
import com.acme.application.shared.code.UpdateApplicationCodePermission;
import com.acme.application.shared.text.ITextService;

@FormData(value = ApplicationCodeFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class ApplicationCodeForm extends AbstractForm {

	public enum DisplayMode {
		CREATE,
		EDIT;
	}	

	private String codeTypeId;

	@FormData
	public String getCodeTypeId() {
		return codeTypeId;
	}

	@FormData
	public void setCodeTypeId(String codeTypeId) {
		this.codeTypeId = codeTypeId;
	}

	public void setDisplayMode(DisplayMode mode) {
		getCodeTextField().setMandatory(true);

		switch (mode) {
		case CREATE:
			getCodeIdField().setEnabled(false);
			getCodeIdField().setMandatory(true);
			break;

		default:
			getCodeIdField().setEnabled(false);
			getCodeIdField().setMandatory(false);
			break;
		}
	}

	@Override
	public Object computeExclusiveKey() {
		String typeId = getCodeTypeId();
		String codeId = getCodeIdField().getValue();
		return String.format("%s %s", typeId, codeId); 
	}

	protected String calculateSubTitle() {
		String typeId = getCodeTypeId();
		String codeId = getCodeIdField().getValue();
		String locale = ClientSession.get().getLocale().toLanguageTag();
		String typeText = BEANS.get(ITextService.class).getText(typeId, locale);
		String codeText = BEANS.get(ITextService.class).getText(codeId, locale);
		
		if(codeText == null) {
			codeText = String.format("[%s]", codeId);
		}
		
		return String.format("%s %s", typeText, codeText);
	}

	@Override
	protected int getConfiguredDisplayHint() {
		return IForm.DISPLAY_HINT_VIEW;
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Code");
	}

	public void startModify() {
		startInternalExclusive(new ModifyHandler());
	}

	public void startNew() {
		startInternal(new NewHandler());
	}

	public CodeIdField getCodeIdField() {
		return getFieldByClass(CodeIdField.class);
	}

	public CodeTextField getCodeTextField() {
		return getFieldByClass(CodeTextField.class);
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	public OrderField getOrderField() {
		return getFieldByClass(OrderField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	public PermissionTableField getPermissionTableField() {
		return getFieldByClass(PermissionTableField.class);
	}

	public RoleBox getRoleBox() {
		return getFieldByClass(RoleBox.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {

		@Order(1000)
		public class CodeBox extends AbstractGroupBox {

			@Order(1000)
			public class CodeTextField extends AbstractStringField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Text");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

			@Order(2000)
			public class CodeIdField extends AbstractStringField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("CodeId");
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
			public class OrderField extends AbstractBigDecimalField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Order");
				}

				@Override
				protected BigDecimal getConfiguredMinValue() {
					return new BigDecimal("-9999999999999999999");
				}

				@Override
				protected BigDecimal getConfiguredMaxValue() {
					return new BigDecimal("9999999999999999999");
				}
			}

			@Order(3000)
			public class ActiveField extends AbstractBooleanField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Active");
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
			load(new UpdateApplicationCodePermission());
		}

		@Override
		protected void execStore() {
			store();
		}

		@Override
		protected void execDirtyStatusChanged(boolean dirty) {
			setSubTitle(calculateSubTitle());
		}

		@Override
		protected boolean getConfiguredOpenExclusive() {
			return true;
		}
	}

	public class NewHandler extends AbstractDirtyFormHandler {

		@Override
		protected void execLoad() {
			setEnabledPermission(new CreateApplicationCodePermission());

			ApplicationCodeFormData formData = new ApplicationCodeFormData();
			exportFormData(formData);
			formData = BEANS.get(IApplicationCodeService.class).load(formData);
			importFormData(formData);

			getForm().setSubTitle(calculateSubTitle());
		}

		@Override
		protected void execStore() {
			store();
		}

		@Override
		protected void execDirtyStatusChanged(boolean dirty) {
			getForm().setSubTitle(calculateSubTitle());
		}
	}

	private void load(Permission permission)  {
		setEnabledPermission(permission);

		ApplicationCodeFormData formData = new ApplicationCodeFormData();
		exportFormData(formData);
		formData = BEANS.get(IApplicationCodeService.class).load(formData);
		importFormData(formData);

		setSubTitle(calculateSubTitle());
	}

	private void store()  {
		ApplicationCodeFormData formData = new ApplicationCodeFormData();
		exportFormData(formData);
		formData = BEANS.get(IApplicationCodeService.class).store(formData);
		ApplicationCodeUtility.reload(formData.getCodeTypeId());
	}
}
