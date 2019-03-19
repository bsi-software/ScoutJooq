#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.booking;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.database.or.core.tables.Booking;
import ${package}.database.or.core.tables.records.BookingRecord;
import ${package}.database.table.TableUtility;
import ${package}.server.ServerSession;
import ${package}.server.common.AbstractBaseService;
import ${package}.server.document.BookingDocumentService;
import ${package}.shared.booking.BookingFormData;
import ${package}.shared.booking.BookingTablePageData;
import ${package}.shared.booking.BookingTablePageData.BookingTableRowData;
import ${package}.shared.booking.IBookingService;
import ${package}.shared.document.IDocumentService;

public class BookingService extends AbstractBaseService<Booking, BookingRecord>  implements IBookingService {

	@Override
	public Booking getTable() {
		return Booking.BOOKING;
	}

	@Override
	public Field<String> getIdColumn() {
		return Booking.BOOKING.ID;
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(BookingService.class);
	}

	@Override
	public BookingTablePageData getBookingTableData(SearchFilter filter) {
		BookingTablePageData pageData = new BookingTablePageData();

		getAll()
		.stream()
		.forEach(booking -> recordToRowData(pageData.addRow(), booking));

		return pageData;
	}

	@Override
	public BookingFormData load(BookingFormData formData) {
		BookingRecord booking = getOrCreate(formData.getBookingId().getValue());
		recordToFormData(formData, booking);
		formData.getDocumentTable().setRows(BEANS.get(IDocumentService.class).getDocumentTableData(booking.getId()).getRows());
		return formData;
	}

	@Override
	public BookingFormData store(BookingFormData formData) {
		String bookingId = formData.getBookingId().getValue();
		BookingRecord booking = getOrCreate(bookingId);
		formDataToRecord(formData, booking);
		store(booking.getId(), booking);
		return formData;
	}

	@Override
	public int delete(String id) {
		int delete = super.delete(id);
		if (delete > 0) {
			BEANS.get(BookingDocumentService.class).delete(id);
		}
		return delete;
	}


	public BookingRecord getOrCreate(String bookingId) {
		BookingRecord booking = get(bookingId);

        if (booking != null) {
            return booking;
        }

        booking = new BookingRecord();
        booking.setId(TableUtility.createId());
        booking.setActive(true);
        booking.setUserId(ServerSession.get().getUserId());

        return booking;
    }

	private void formDataToRecord(BookingFormData formData, BookingRecord booking) {
		if (booking != null && formData != null) {
			booking.setDescription(formData.getDescription().getValue());
			booking.setDateFrom(formData.getDateFrom().getValue());
			booking.setDateTo(formData.getDateTo().getValue());
			booking.setNote(formData.getNote().getValue());
			booking.setUserId(formData.getUserId().getValue());
			booking.setActive(formData.getActive().getValue());
		}
	}

	private void recordToFormData(BookingFormData formData, BookingRecord booking) {
		if (booking != null && formData != null) {
			setValueWhenNull(formData.getBookingId(), booking.getId());
			setValueWhenNull(formData.getDescription(), booking.getDescription());
			setValueWhenNull(formData.getDateFrom(), booking.getDateFrom());
			setValueWhenNull(formData.getDateTo(), booking.getDateTo());
			setValueWhenNull(formData.getNote(), booking.getNote());
			setValueWhenNull(formData.getUserId(), booking.getUserId());
			formData.getActive().setValue(booking.getActive());
		}
	}

	private <T> void setValueWhenNull(AbstractValueFieldData<T> field, T value) {
		if (field.getValue() == null) {
			field.setValue(value);
		}
	}

	private void recordToRowData(BookingTableRowData row, BookingRecord booking) {
		row.setId(booking.getId());
		row.setDescription(booking.getDescription());
		row.setDateFrom(booking.getDateFrom());
		row.setDateTo(booking.getDateTo());
		row.setNote(booking.getNote());
		row.setUser(booking.getUserId());
		row.setActive(booking.getActive());
	}

}
