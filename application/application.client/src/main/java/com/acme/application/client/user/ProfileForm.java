package com.acme.application.client.user;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.AbstractFormField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractLinkButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

import com.acme.application.client.ClientSession;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox.CancelPasswordChangeLink;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox.ChangePasswordLink;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox.ConfirmPasswordField;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox.NewPasswordField;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox.OldPasswordField;
import com.acme.application.client.user.ProfileForm.MainBox.ChangePasswordBox.UpdateLinkButton;
import com.acme.application.client.user.ProfileForm.MainBox.OkButton;
import com.acme.application.client.user.ProfileForm.MainBox.OptionUserBox;
import com.acme.application.shared.user.IUserService;
import com.acme.application.shared.user.ProfileFormData;

@FormData(value = ProfileFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class ProfileForm extends AbstractForm {

	public ProfileForm() {
		super();
		setUserId(ClientSession.get().getUserId());
	}

	public String getUserId() {
		return getUserBox().getUserIdField().getValue();
	}

	public void setUserId(String userId) {
		getUserBox().getUserIdField().setValue(userId);
	}

	public OptionUserBox getUserBox() {
		return getFieldByClass(OptionUserBox.class);
	}

	public OldPasswordField getOldPasswordField() {
		return getFieldByClass(OldPasswordField.class);
	}

	public CancelPasswordChangeLink getCancelPasswordChangeField() {
		return getFieldByClass(CancelPasswordChangeLink.class);
	}

	public UpdateLinkButton getVerifyLinkButton() {
		return getFieldByClass(UpdateLinkButton.class);
	}

	public NewPasswordField getNewPasswordField() {
		return getFieldByClass(NewPasswordField.class);
	}

	public ConfirmPasswordField getConfirmPasswordField() {
		return getFieldByClass(ConfirmPasswordField.class);
	}

	public ChangePasswordLink getChangePasswordLink() {
		return getFieldByClass(ChangePasswordLink.class);
	}

	public ChangePasswordBox getChangePasswordBox() {
		return getFieldByClass(ChangePasswordBox.class);
	}

	public OkButton getApplyButton() {
		return getFieldByClass(OkButton.class);
	}

	@Override
	public void start() {
		startInternal(new ModifyHandler());
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Profile");
	}
	
	public void startModify() {
		getUserBox().setUserIdEnabled(false);
		startInternalExclusive(new ModifyHandler());
	}

	@Order(10)
	public class MainBox extends AbstractGroupBox {

		@Order(10)
		public class OptionUserBox extends AbstractUserBox {

		}

		@Order(20)
		public class ChangePasswordBox extends AbstractSequenceBox {

			boolean changeIt = false;

			public boolean getValue() {
				return changeIt;
			}

			public void setValue(boolean changeIt) {
				this.changeIt = changeIt;

				getApplyButton().setEnabled(!changeIt);
				getChangePasswordLink().setVisible(!changeIt);
				getOldPasswordField().setVisible(changeIt);
				getNewPasswordField().setVisible(changeIt);
				getConfirmPasswordField().setVisible(changeIt);
				getVerifyLinkButton().setVisible(changeIt);
				getCancelPasswordChangeField().setVisible(changeIt);

				resetPasswordFieldValues();
				clearPasswordErrorStates();
			}

			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Password");
			}

			@Override
			protected boolean getConfiguredAutoCheckFromTo() {
				return false;
			}

			@Override
			protected int getConfiguredGridW() {
				return 2;
			}

			@Override
			protected void execInitField() {
				setValue(false);
			}

			@Order(0)
			public class ChangePasswordLink extends AbstractLinkButton {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("ChangePassword");
				}

				@Override
				protected void execClickAction() {
					getApplyButton().setEnabled(false);
					setValue(true);
				}
			}

			@Order(10)
			public class OldPasswordField extends AbstractPasswordField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("OldPassword");
				}

				@Override
				protected byte getConfiguredLabelPosition() {
					return AbstractFormField.LABEL_POSITION_ON_FIELD;
				}
			}

			@Order(20)
			public class NewPasswordField extends AbstractPasswordField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("NewPassword");
				}

				@Override
				protected byte getConfiguredLabelPosition() {
					return AbstractFormField.LABEL_POSITION_ON_FIELD;
				}
			}

			@Order(30)
			public class ConfirmPasswordField extends AbstractPasswordField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("ConfirmPassword");
				}

				@Override
				protected byte getConfiguredLabelPosition() {
					return AbstractFormField.LABEL_POSITION_ON_FIELD;
				}
			}

			@Order(40)
			public class UpdateLinkButton extends AbstractLinkButton {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Update");
				}

				@Override
				protected String getConfiguredTooltipText() {
					return TEXTS.get("ClickToEnableApplyButton");
				}

				@Override
				protected void execClickAction() {
					if (validatePasswordChange()) {
						getApplyButton().setEnabled(true);
						getOldPasswordField().setEnabled(false);
						getNewPasswordField().setEnabled(false);
						getConfirmPasswordField().setEnabled(false);
					}
				}
			}

			@Order(50)
			public class CancelPasswordChangeLink extends AbstractLinkButton {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("CancelPasswordChange");
				}

				@Override
				protected void execClickAction() {
					setValue(false);
					getApplyButton().setEnabled(true);
					getOldPasswordField().setEnabled(true);
					getNewPasswordField().setEnabled(true);
					getConfirmPasswordField().setEnabled(true);
				}
			}
		}

		@Order(100)
		public class OkButton extends AbstractOkButton {

			@Override
			protected void execClickAction() {
				if (getChangePasswordBox().getValue()) {
					if (!validatePasswordChange()) {
						return;
					}
				}

				super.execClickAction();
			}
		}

		private boolean validatePasswordChange() {
			clearPasswordErrorStates();

			if (!getChangePasswordBox().getValue()) {
				return true;
			}

			boolean ok = true;
			ok &= validateOldPassword();
			ok &= validateNewPassword();
			ok &= validateConfirmPassword();

			return ok;
		}

		private void resetPasswordFieldValues() {
			getOldPasswordField().setValue(null);
			getNewPasswordField().setValue(null);
			getConfirmPasswordField().setValue(null);
		}

		private void clearPasswordErrorStates() {
			getOldPasswordField().clearErrorStatus();
			getNewPasswordField().clearErrorStatus();
			getConfirmPasswordField().clearErrorStatus();
		}

		private boolean validateOldPassword() {
			String username = getUserId();
			String password = getOldPasswordField().getValue();

			if (!BEANS.get(IUserService.class).verifyPassword(username, password)) {
				getOldPasswordField().setError(TEXTS.get("PasswordInvalid"));
				return false;
			}

			return true;
		}

		private boolean validateNewPassword() {
			return getNewPasswordField().validateField();
		}

		private boolean validateConfirmPassword() {
			if (!getConfirmPasswordField().validateField()) {
				return false;
			}

			String passwordConfirm = getConfirmPasswordField().getValue();
			String passwordNew = getNewPasswordField().getValue();

			if (!passwordConfirm.equals(passwordNew)) {
				getConfirmPasswordField().setError(TEXTS.get("PasswordMismatchError"));
				return false;
			}

			return true;
		}

		@Order(110)
		public class CancelButton extends AbstractCancelButton {
		}
	}

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			reload();
		}

		@Override
		protected void execStore() {
			IUserService service = BEANS.get(IUserService.class);
			ProfileFormData formData = new ProfileFormData();
			exportFormData(formData);
			service.store(formData);
			
			// FIXME does not live refresh language to new locale
			ClientSession.get().initializeLocaleTextsAndCodes();
		}
	}

	/**
	 * Reload needs to be public as it is triggered from outside while the form
	 * is still open (but invisible).
	 */
	public void reload() {
		IUserService service = BEANS.get(IUserService.class);
		ProfileFormData formData = new ProfileFormData();
		exportFormData(formData);
		formData = service.load(formData);
		importFormData(formData);
	}
}
