#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.document;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;

import ${package}.shared.document.IDocumentService;

public class DocumentLookupCall extends LocalLookupCall<String> {

	private static final long serialVersionUID = 1L;

	@Override
	protected List<? extends ILookupRow<String>> execCreateLookupRows() {
		return BEANS.get(IDocumentService.class).getLookupRows(false);
	}
}
