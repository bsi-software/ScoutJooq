#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.document;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface IDocumentService extends IService {

	DocumentTablePageData getDocumentTableData(SearchFilter filter);
	
	BinaryResource getResource(String documentId);

	void store(String name, byte[] content, String username);
}
