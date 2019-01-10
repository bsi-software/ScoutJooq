package com.acme.application.shared.payment;

import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface IPaymentService {

	AbstractTablePageData getPaymentTableData(SearchFilter filter);

	PaymentFormData load(PaymentFormData formData);

	PaymentFormData store(PaymentFormData formData);

	int delete(String id);
}
