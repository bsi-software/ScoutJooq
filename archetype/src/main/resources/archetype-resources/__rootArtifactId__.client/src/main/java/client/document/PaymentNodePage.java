#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.document;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.classid.ClassId;

@ClassId("37c402c4-165e-460c-b6e5-6215a8fb8909")
public class PaymentNodePage extends AbstractPageWithNodes {

	private String paymentId;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		DocumentTablePage documentTablePage = new DocumentTablePage();
		documentTablePage.setPaymentId(getPaymentId());
		pageList.add(documentTablePage);
	}

}
