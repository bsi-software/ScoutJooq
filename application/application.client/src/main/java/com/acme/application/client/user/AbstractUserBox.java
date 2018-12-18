package com.acme.application.client.user;

import java.util.Locale;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;

import com.acme.application.shared.code.LocaleCodeType;
import com.acme.application.shared.user.AbstractUserBoxData;

@FormData(value = AbstractUserBoxData.class, sdkCommand = FormData.SdkCommand.CREATE, defaultSubtypeSdkCommand = FormData.DefaultSubtypeSdkCommand.CREATE)
public abstract class AbstractUserBox extends AbstractGroupBox {

	private String userId;

	private String firstName;

	private String lastName;

	private Locale locale;


	@FormData
	public String getUserId() {
		return userId;
	}

	@FormData
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@FormData
	public String getFirstName() {
		return firstName;
	}

	@FormData
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@FormData
	public String getLastName() {
		return lastName;
	}

	@FormData
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@FormData
	public Locale getLocale() {
		return locale;
	}

	@FormData
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public FirstNameField getFirstNameField() {
		return getFieldByClass(FirstNameField.class);
	}

	public LastNameField getLastNameField() {
		return getFieldByClass(LastNameField.class);
	}

	public UserIdField getUserIdField() {
		return getFieldByClass(UserIdField.class);
	}

	public LocaleField getLocaleField() {
		return getFieldByClass(LocaleField.class);
	}

	@Override
	protected double getConfiguredGridWeightY() {
		return 0;
	}


	@Order(10)
	public class FirstNameField extends AbstractStringField {
		@Override
		protected String getConfiguredLabel() {
			return TEXTS.get("FirstName");
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

	@Order(20)
	public class LastNameField extends AbstractStringField {
		@Override
		protected String getConfiguredLabel() {
			return TEXTS.get("LastName");
		}

		@Override
		protected int getConfiguredMaxLength() {
			return 128;
		}
	}

	@Order(30)
	public class LocaleField extends AbstractSmartField<String> {
		@Override
		protected String getConfiguredLabel() {
			return TEXTS.get("Language");
		}

		@Override
		protected boolean getConfiguredMandatory() {
			return true;
		}
		
		@Override
		protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
			return LocaleCodeType.class;
		}
	}

	@Order(40)
	public class UserIdField extends AbstractStringField {
		@Override
		protected String getConfiguredLabel() {
			return TEXTS.get("UserName");
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
	
	public void setUserIdEnabled(boolean enabled) {
		getUserIdField().setEnabled(enabled);
		getUserIdField().setMandatory(enabled);
	}
}
