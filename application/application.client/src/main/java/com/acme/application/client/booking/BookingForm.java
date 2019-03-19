package com.acme.application.client.booking;

import java.util.Date;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.datefield.AbstractDateTimeField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.acme.application.client.booking.BookingForm.MainBox.BookingBox;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.CancelButton;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.DescriptionBox;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.DescriptionBox.DescriptionField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.DescriptionBox.UserIdField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.InformationBox;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.InformationBox.DocumentTableField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.InformationBox.NoteField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.OkButton;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.TechnicalBox;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.TechnicalBox.ActiveField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.TechnicalBox.BookingIdField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.TimeBox;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.TimeBox.DateFromField;
import com.acme.application.client.booking.BookingForm.MainBox.BookingBox.TimeBox.DateToField;
import com.acme.application.client.document.AbstractDocumentTable;
import com.acme.application.client.user.UserLookupCall;
import com.acme.application.shared.booking.BookingFormData;
import com.acme.application.shared.booking.BookingFormData.DocumentTable;
import com.acme.application.shared.booking.CreateBookingPermission;
import com.acme.application.shared.booking.IBookingService;
import com.acme.application.shared.booking.UpdateBookingPermission;

@ClassId("d0e79008-7b79-42f2-a1b7-196ef2d3a3a8")
@FormData(value = BookingFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class BookingForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Booking");
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

	public DescriptionField getUsageField() {
		return getFieldByClass(DescriptionField.class);
	}

	public DateFromField getDateField() {
		return getFieldByClass(DateFromField.class);
	}

	public BookingIdField getBookingIdField() {
		return getFieldByClass(BookingIdField.class);
	}

	public UserIdField getUserIdField() {
		return getFieldByClass(UserIdField.class);
	}

	public ActiveField getActiveField() {
		return getFieldByClass(ActiveField.class);
	}

	public BookingBox getBookingBox() {
		return getFieldByClass(BookingBox.class);
	}

	public DocumentTableField getDocumentTableField() {
		return getFieldByClass(DocumentTableField.class);
	}

	public DescriptionBox getDescriptionBox() {
		return getFieldByClass(DescriptionBox.class);
	}

	public TimeBox getMoneyBox() {
		return getFieldByClass(TimeBox.class);
	}

	public TechnicalBox getTechnicalBox() {
		return getFieldByClass(TechnicalBox.class);
	}

	public InformationBox getInformationBox() {
		return getFieldByClass(InformationBox.class);
	}

	public DateToField getDateToField() {
		return getFieldByClass(DateToField.class);
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
		public class BookingBox extends AbstractGroupBox {
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
				public class DescriptionField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Description");
					}

					@Override
					protected int getConfiguredMaxLength() {
						return 128;
					}
				}

				@Order(2000)
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
			}

			@Order(2000)
			@ClassId("661c213e-1be9-44a6-94f1-ccfe0b5f99e9")
			public class TimeBox extends AbstractGroupBox {
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
				@ClassId("942bf118-5638-4639-9188-cdb4116a997d")
				public class DateFromField extends AbstractDateTimeField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("DateFrom");
					}

					@Override
					protected Date execValidateValue(Date rawValue) {
						Date to = getDateToField().getValue();
						if (to != null && rawValue.after(to)) {
							throw new VetoException(TEXTS.get("DateFromViolation"));
						}
						return super.execValidateValue(rawValue);
					}

					@Override
					protected void execChangedValue() {
						getDateToField().clearErrorStatus();
					}
				}

				@Order(2000)
				public class DateToField extends AbstractDateTimeField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("DateTo");
					}

					@Override
					protected Date execValidateValue(Date rawValue) {
						Date from = getDateField().getValue();
						if (from != null && rawValue.before(from)) {
							throw new VetoException(TEXTS.get("DateToViolation"));
						}
						return super.execValidateValue(rawValue);
					}

					@Override
					protected void execChangedValue() {
						getDateField().clearErrorStatus();
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
				@ClassId("4d590276-8f55-43df-9738-ad277cb07618")
				public class ActiveField extends AbstractBooleanField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Active");
					}
				}

				@Order(2000)
				@ClassId("4634fae4-1fda-4b38-b108-ddc7bc8fe859")
				public class BookingIdField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("BookingId");
					}

					@Override
					protected boolean getConfiguredVisible() {
						return false;
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
						protected void reloadTableData() {
							DocumentTableField.this.reloadTableData();
						}

						@Override
						protected String getBookingId() {
							return getBookingIdField().getValue();
						}

						@Override
						public IOutline getPageOutline() {
							return null;
						}

						@Override
						protected void importFieldData(DocumentTable documentTable) {
							documentTable.setValueSet(true);
							importFormFieldData(documentTable, false);
						}
					}

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("DocumentTablePage");
					}
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
			setEnabledPermission(new UpdateBookingPermission());
			BookingFormData formData = new BookingFormData();
			exportFormData(formData);
			formData = BEANS.get(IBookingService.class).load(formData);
			importFormData(formData);
		}

		@Override
		protected void execStore() {
			BookingFormData formData = new BookingFormData();
			exportFormData(formData);
			BEANS.get(IBookingService.class).store(formData);
		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			setEnabledPermission(new CreateBookingPermission());
			BookingFormData formData = new BookingFormData();
			exportFormData(formData);
			importFormData(BEANS.get(IBookingService.class).load(formData));
		}

		@Override
		protected void execStore() {
			BookingFormData formData = new BookingFormData();
			exportFormData(formData);
			formData = BEANS.get(IBookingService.class).store(formData);
			importFormData(formData);
		}
	}
}
