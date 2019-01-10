#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.document;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import ${package}.client.document.DocumentTablePage.Table;
import ${package}.shared.document.DocumentTablePageData;
import ${package}.shared.document.IDocumentService;
import ${package}.shared.payment.PaymentFormData.DocumentTable;

@Data(DocumentTablePageData.class)
public class DocumentTablePage extends AbstractPageWithTable<Table> {

	private String paymentId;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("DocumentTablePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IDocumentService.class).getDocumentTableData(filter, getPaymentId()));
	}

	public class Table extends AbstractDocumentTable  {
		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		@Override
		protected void importFieldData(DocumentTable documentTable) {
			// Not needed, as we do that on page reload
		}

		@Override
		protected void reloadTableData() {
			reloadPage();
		}

		@Override
		protected String getPaymentId() {
			return DocumentTablePage.this.getPaymentId();
		}
	}
}
