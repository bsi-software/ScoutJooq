#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.payment;

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
