package com.acme.application.client.payment;

import java.math.BigDecimal;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.bigdecimalfield.AbstractBigDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.datefield.AbstractDateField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.acme.application.client.document.AbstractDocumentTable;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.CancelButton;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.DescriptionBox;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.DescriptionBox.DateField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.DescriptionBox.UsageField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.InformationBox;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.InformationBox.DocumentTableField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.InformationBox.NoteField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.MoneyBox;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.MoneyBox.AmountField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.MoneyBox.SharedField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.OkButton;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.PaymentIdField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.TechnicalBox;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.TechnicalBox.ActiveField;
import com.acme.application.client.payment.PaymentForm.MainBox.PaymentBox.TechnicalBox.UserIdField;
import com.acme.application.client.user.UserLookupCall;
import com.acme.application.shared.payment.CreatePaymentPermission;
import com.acme.application.shared.payment.IPaymentService;
import com.acme.application.shared.payment.PaymentFormData;
import com.acme.application.shared.payment.PaymentFormData.DocumentTable;
import com.acme.application.shared.payment.UpdatePaymentPermission;

@ClassId("d0e79008-7b79-42f2-a1b7-196ef2d3a3a8")
@FormData(value = PaymentFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class PaymentForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Payment");
	}

	@Override
	protected int getConfiguredDisplayHint() {
		return DISPLAY_HINT_VIEW;
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

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public UsageField getUsageField() {
		return getFieldByClass(UsageField.class);
	}

	public DateField getDateField() {
		return getFieldByClass(DateField.class);
	}

	public AmountField getAmountField() {
		return getFieldByClass(AmountField.class);
	}

	public SharedField getSharedField() {
		return getFieldByClass(SharedField.class);
	}

	public PaymentIdField getPaymentIdField() {
		return getFieldByClass(PaymentIdField.class);
	}

	public UserIdField getUserIdField() {
		return getFieldByClass(UserIdField.class);
	}

	public ActiveField getActiveField() {
		return getFieldByClass(ActiveField.class);
	}

	public PaymentBox getPaymentBox() {
		return getFieldByClass(PaymentBox.class);
	}

	public DocumentTableField getDocumentTableField() {
		return getFieldByClass(DocumentTableField.class);
	}

	public DescriptionBox getDescriptionBox() {
		return getFieldByClass(DescriptionBox.class);
	}

	public MoneyBox getMoneyBox() {
		return getFieldByClass(MoneyBox.class);
	}

	public TechnicalBox getTechnicalBox() {
		return getFieldByClass(TechnicalBox.class);
	}

	public InformationBox getInformationBox() {
		return getFieldByClass(InformationBox.class);
	}

	public NoteField getNoteField() {
		return getFieldByClass(NoteField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(1000)
	@ClassId("bba73e33-65b1-4f47-b862-9145022c7b49")
	public class MainBox extends AbstractGroupBox {

		@Order(1000)
		@ClassId("a9a9cad7-91bd-4254-868e-51ebf2ff0756")
		public class PaymentBox extends AbstractGroupBox {
			@Override
			protected boolean getConfiguredLabelVisible() {
				return false;
			}

			@Order(1000)
			@ClassId("bbe18116-3400-4eb3-9e60-651d57fb1260")
			public class DescriptionBox extends AbstractGroupBox {
				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}

				@Override
				protected int getConfiguredGridH() {
					return 1;
				}

				@Override
				protected int getConfiguredGridW() {
					return 2;
				}

				@Order(1000)
				@ClassId("4a2b87fb-6ec0-4c8a-aac6-670787005d09")
				public class UsageField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Usage");
					}

					@Override
					protected int getConfiguredMaxLength() {
						return 128;
					}
				}

				@Order(2000)
				@ClassId("942bf118-5638-4639-9188-cdb4116a997d")
				public class DateField extends AbstractDateField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Date");
					}
				}
			}

			@Order(2000)
			@ClassId("661c213e-1be9-44a6-94f1-ccfe0b5f99e9")
			public class MoneyBox extends AbstractGroupBox {
				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}

				@Override
				protected int getConfiguredGridH() {
					return 1;
				}

				@Override
				protected int getConfiguredGridW() {
					return 2;
				}

				@Order(1000)
				@ClassId("81392d71-e2f5-411c-88c7-e70452f73fa8")
				public class AmountField extends AbstractBigDecimalField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Amount");
					}

					@Override
					protected BigDecimal getConfiguredMinValue() {
						return new BigDecimal("0");
					}

					@Override
					protected BigDecimal getConfiguredMaxValue() {
						return new BigDecimal("10000");
					}
				}

				@Order(2000)
				@ClassId("00a6b2ce-a622-4017-a53e-3cd6d13fe2b1")
				public class SharedField extends AbstractBooleanField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Shared");
					}
				}
			}

			@Order(3000)
			@ClassId("b1ded1f2-cf59-41c8-8a48-869e88d2c373")
			public class TechnicalBox extends AbstractGroupBox {
				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}

				@Override
				protected int getConfiguredGridH() {
					return 1;
				}

				@Override
				protected int getConfiguredGridW() {
					return 2;
				}

				@Order(1000)
				@ClassId("660d437a-0ad7-4ec1-a3f4-be264be64905")
				public class UserIdField extends AbstractSmartField<String> {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("User");
					}

					@Override
					protected boolean getConfiguredEnabled() {
						return false;
					}

					@Override
					protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
						return UserLookupCall.class;
					}
				}

				@Order(2000)
				@ClassId("4d590276-8f55-43df-9738-ad277cb07618")
				public class ActiveField extends AbstractBooleanField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Active");
					}
				}
			}

			@Order(4000)
			@ClassId("5741c879-0b77-4bfb-a5d7-6bc1d8bb839c")
			public class InformationBox extends AbstractGroupBox {

				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}

				@Order(1000)
				@ClassId("5ad99ca1-dee6-4d6b-916c-b23f38044626")
				public class NoteField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Note");
					}

					@Override
					protected boolean getConfiguredMultilineText() {
						return true;
					}

					@Override
					protected int getConfiguredGridH() {
						return 3;
					}

					@Override
					protected double getConfiguredGridWeightY() {
						return 1;
					}

					@Override
					protected int getConfiguredMaxLength() {
						return 128;
					}
				}

				@Order(2000)
				@ClassId("4a3f8a31-f4d5-4b63-8764-078394eb3b0f")
				public class DocumentTableField extends AbstractTableField<DocumentTableField.Table> {

					@ClassId("a39cb82a-5191-45ff-aa09-43cf2b5cf127")
					public class Table extends AbstractDocumentTable {

						@Override
						protected void importFieldData(DocumentTable documentTable) {
							importFormFieldData(documentTable, false);

						}

						@Override
						protected void reloadTableData() {
							DocumentTableField.this.reloadTableData();
						}

						@Override
						protected String getPaymentId() {
							return getPaymentIdField().getValue();
						}

						@Override
						public IOutline getPageOutline() {
							return null;
						}
					}

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("DocumentTablePage");
					}
				}
			}

			@Order(5000)
			@ClassId("4634fae4-1fda-4b38-b108-ddc7bc8fe859")
			public class PaymentIdField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("PaymentId");
				}

				@Override
				protected boolean getConfiguredVisible() {
					return false;
				}
			}

			@Order(6000)
			@ClassId("c970107a-94d7-423b-bc43-3859ebf5dd0b")
			public class OkButton extends AbstractOkButton {
			}

			@Order(7000)
			@ClassId("683e7d02-15ab-4230-b40f-f1f2b9915e09")
			public class CancelButton extends AbstractCancelButton {
			}
		}

	}

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			setEnabledPermission(new UpdatePaymentPermission());
			PaymentFormData formData = new PaymentFormData();
			exportFormData(formData);
			formData = BEANS.get(IPaymentService.class).load(formData);
			importFormData(formData);
		}

		@Override
		protected void execStore() {
			PaymentFormData formData = new PaymentFormData();
			exportFormData(formData);
			BEANS.get(IPaymentService.class).store(formData);
		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			setEnabledPermission(new CreatePaymentPermission());
			PaymentFormData formData = new PaymentFormData();
			exportFormData(formData);
			importFormData(BEANS.get(IPaymentService.class).load(formData));
		}

		@Override
		protected void execStore() {
			PaymentFormData formData = new PaymentFormData();
			exportFormData(formData);
			formData = BEANS.get(IPaymentService.class).store(formData);
			importFormData(formData);
		}
	}
}
