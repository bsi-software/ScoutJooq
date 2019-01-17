package com.acme.application.shared.document;

import java.util.List;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;

import com.acme.application.shared.booking.BookingFormData.DocumentTable;

@TunnelToServer
public interface IDocumentService extends IService {

	DocumentTablePageData getDocumentTableData(SearchFilter filter, String string);

	DocumentTable getDocumentTableData(String string);

	BinaryResource getResource(String documentId);

	void store(String name, byte[] content, String username, String string);

	/**
	 * Returns all documents as lookup rows.
	 * @param activeOnly: restricts result set to active documents, if active is true
	 */
	public List<? extends ILookupRow<String>> getLookupRows(boolean activeOnly);

	int delete(String id);
}
