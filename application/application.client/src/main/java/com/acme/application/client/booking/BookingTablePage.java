package com.acme.application.client.booking;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateTimeColumn;
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

import com.acme.application.client.booking.BookingTablePage.BookingTable;
import com.acme.application.client.common.AbstractActiveColumn;
import com.acme.application.client.common.AbstractDeleteMenu;
import com.acme.application.client.common.AbstractEditMenu;
import com.acme.application.client.common.AbstractExportableTable;
import com.acme.application.client.common.AbstractIdColumn;
import com.acme.application.client.common.AbstractNewMenu;
import com.acme.application.client.document.BookingNodePage;
import com.acme.application.client.user.UserLookupCall;
import com.acme.application.shared.booking.BookingTablePageData;
import com.acme.application.shared.booking.IBookingService;

@ClassId("1d16a8e6-667c-4bb7-8729-9110ba359e44")
@Data(BookingTablePageData.class)
public class BookingTablePage extends AbstractPageWithTable<BookingTable> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("BookingTablePage");
	}

	@Override
	protected IPage<?> execCreateChildPage(ITableRow row) {
		BookingNodePage bookingNodePage = new BookingNodePage();
		bookingNodePage.setBookingId(getTable().getIdColumn().getValue(row));
		return bookingNodePage;
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IBookingService.class).getBookingTableData(filter));
	}

	public class BookingTable extends AbstractExportableTable  {

		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		public DateFromColumn getDateColumn() {
			return getColumnSet().getColumnByClass(DateFromColumn.class);
		}

		public DescriptionColumn getUsageColumn() {
			return getColumnSet().getColumnByClass(DescriptionColumn.class);
		}

		public ActiveColumn getActiveColumn() {
			return getColumnSet().getColumnByClass(ActiveColumn.class);
		}

		public UserColumn getUserColumn() {
			return getColumnSet().getColumnByClass(UserColumn.class);
		}

		public DateToColumn getDateToColumn() {
			return getColumnSet().getColumnByClass(DateToColumn.class);
		}

		public NoteColumn getNoteColumn() {
			return getColumnSet().getColumnByClass(NoteColumn.class);
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
		public class DateFromColumn extends AbstractDateTimeColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("DateFrom");
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

		@Order(2500)
		public class DateToColumn extends AbstractDateTimeColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("DateTo");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(3000)
		@ClassId("2d2168d8-c18f-4a2e-b30b-aa1ef6ae986f")
		public class DescriptionColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Description");
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
				return TEXTS.get("NewBooking");
			}

			@Override
			protected void execAction() {
				BookingForm bookingForm = new BookingForm();
				bookingForm.addFormListener(new BookingFormListener());
				bookingForm.startNew();
			}
		}

		@Order(2000)
		@ClassId("ee050107-6572-4a01-8359-447b98521050")
		public class EditMenu extends AbstractEditMenu {
			@Override
			protected void execAction() {
				String id = getTable().getIdColumn().getSelectedValue();
				BookingForm bookingForm = new BookingForm();
				bookingForm.addFormListener(new BookingFormListener());
				bookingForm.getBookingIdField().setValue(id);
				bookingForm.startModify();
			}
		}

		@Order(5000)
		@ClassId("9ea1b247-8868-4cbe-994c-d06ab4c7ba5d")
		public class BookingDeleteMenu extends AbstractDeleteMenu {
			@Override
			protected void execAction() {
				IBookingService service = BEANS.get(IBookingService.class);
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

	protected class BookingFormListener implements FormListener {
		@Override
		public void formChanged(FormEvent e) {
			if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
				reloadPage();
			}
		}
	}
}
