package com.acme.application.shared.booking;

import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.acme.application.shared.booking.BookingFormData;

@TunnelToServer
public interface IBookingService {

	AbstractTablePageData getBookingTableData(SearchFilter filter);

	BookingFormData load(BookingFormData formData);

	BookingFormData store(BookingFormData formData);

	int delete(String id);
}
