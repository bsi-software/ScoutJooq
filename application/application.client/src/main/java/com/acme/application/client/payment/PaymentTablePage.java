package com.acme.application.client.payment;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBigDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.acme.application.client.common.AbstractActiveColumn;
import com.acme.application.client.common.AbstractDeleteMenu;
import com.acme.application.client.common.AbstractEditMenu;
import com.acme.application.client.common.AbstractExportableTable;
import com.acme.application.client.common.AbstractIdColumn;
import com.acme.application.client.common.AbstractNewMenu;
import com.acme.application.client.document.PaymentNodePage;
import com.acme.application.client.payment.PaymentTablePage.PaymentTable;
import com.acme.application.client.user.UserLookupCall;
import com.acme.application.shared.payment.IPaymentService;
import com.acme.application.shared.payment.PaymentTablePageData;

@ClassId("1d16a8e6-667c-4bb7-8729-9110ba359e44")
@Data(PaymentTablePageData.class)
public class PaymentTablePage extends AbstractPageWithTable<PaymentTable> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("PaymentTablePage");
	}

	@Override
	protected IPage<?> execCreateChildPage(ITableRow row) {
		PaymentNodePage paymentNodePage = new PaymentNodePage();
		paymentNodePage.setPaymentId(getTable().getIdColumn().getValue(row));
		return paymentNodePage;
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IPaymentService.class).getPaymentTableData(filter));
	}

	public class PaymentTable extends AbstractExportableTable  {

		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		public DateColumn getDateColumn() {
			return getColumnSet().getColumnByClass(DateColumn.class);
		}

		public AmountColumn getAmountColumn() {
			return getColumnSet().getColumnByClass(AmountColumn.class);
		}

		public UsageColumn getUsageColumn() {
			return getColumnSet().getColumnByClass(UsageColumn.class);
		}

		public ActiveColumn getActiveColumn() {
			return getColumnSet().getColumnByClass(ActiveColumn.class);
		}

		public UserColumn getUserColumn() {
			return getColumnSet().getColumnByClass(UserColumn.class);
		}

		public NoteColumn getNoteColumn() {
			return getColumnSet().getColumnByClass(NoteColumn.class);
		}

		public SharedColumn getSharedColumn() {
			return getColumnSet().getColumnByClass(SharedColumn.class);
		}

		public IdColumn getIdColumn() {
			return getColumnSet().getColumnByClass(IdColumn.class);
		}

		@Order(1000)
		@ClassId("c7ce0f94-6c4d-4942-b5a7-53e21aaab397")
		public class IdColumn extends AbstractIdColumn {
		}

		@Order(2000)
		@ClassId("b9bd8c35-4a41-46d2-a05c-14406c4d9e86")
		public class DateColumn extends AbstractDateColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Date");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}

			@Override
			protected boolean getConfiguredSummary() {
				return true;
			}
		}

		@Order(3000)
		@ClassId("2d2168d8-c18f-4a2e-b30b-aa1ef6ae986f")
		public class UsageColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Usage");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}

			@Override
			protected boolean getConfiguredSummary() {
				return true;
			}
		}

		@Order(4000)
		@ClassId("816c1b33-70d4-485c-a298-aeb8e4638518")
		public class AmountColumn extends AbstractBigDecimalColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Amount");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(5000)
		@ClassId("fe9bcadf-66cf-495c-bc30-3fe40e3cf1e4")
		public class SharedColumn extends AbstractBooleanColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Shared");
			}

			@Override
			protected int getConfiguredWidth() {
				return 75;
			}
		}

		@Order(6000)
		@ClassId("37453552-60f5-4d83-91d6-b06b7df9b98d")
		public class NoteColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Note");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}

		@Order(7000)
		@ClassId("718dc2ab-f9af-4d03-9910-fb0e96843dc5")
		public class ActiveColumn extends AbstractActiveColumn {
			@Override
			protected boolean getConfiguredVisible() {
				return false;
			}
		}

		@Order(8000)
		@ClassId("c19589f8-78f8-4ea8-8cb1-6aaea0ea7385")
		public class UserColumn extends AbstractSmartColumn<String> {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("User");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}

			@Override
			protected boolean getConfiguredVisible() {
				return false;
			}

			@Override
			protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
				return UserLookupCall.class;
			}
		}

		@Override
		protected Class<? extends IMenu> getConfiguredDefaultMenu() {
			return EditMenu.class;
		}

		@Order(1000)
		@ClassId("978510d0-674d-4298-b8eb-6e34ffa52db4")
		public class NewMenu extends AbstractNewMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("NewPayment");
			}

			@Override
			protected void execAction() {
				PaymentForm paymentForm = new PaymentForm();
				paymentForm.addFormListener(new PaymentFormListener());
				paymentForm.startNew();
			}
		}

		@Order(2000)
		@ClassId("ee050107-6572-4a01-8359-447b98521050")
		public class EditMenu extends AbstractEditMenu {
			@Override
			protected void execAction() {
				String id = getTable().getIdColumn().getSelectedValue();
				PaymentForm paymentForm = new PaymentForm();
				paymentForm.addFormListener(new PaymentFormListener());
				paymentForm.getPaymentIdField().setValue(id);
				paymentForm.startModify();
			}
		}

		@Order(5000)
		@ClassId("9ea1b247-8868-4cbe-994c-d06ab4c7ba5d")
		public class PaymentDeleteMenu extends AbstractDeleteMenu {
			@Override
			protected void execAction() {
				IPaymentService service = BEANS.get(IPaymentService.class);
				getTable().getIdColumn().getSelectedValues()
					.stream()
					.forEach(service::delete);
				reloadPage();
			}

			@Override
			public AbstractSmartColumn<String> getOwner() {
				return getUserColumn();
			}
		}
	}

	protected class PaymentFormListener implements FormListener {
		@Override
		public void formChanged(FormEvent e) {
			if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
				reloadPage();
			}
		}
	}
}
