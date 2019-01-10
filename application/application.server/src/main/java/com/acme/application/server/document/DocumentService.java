package com.acme.application.server.document;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.or.core.tables.Document;
import com.acme.application.database.or.core.tables.PaymentDocument;
import com.acme.application.database.or.core.tables.records.DocumentRecord;
import com.acme.application.database.or.core.tables.records.PaymentDocumentRecord;
import com.acme.application.database.table.TableUtility;
import com.acme.application.server.common.AbstractBaseService;
import com.acme.application.shared.code.FileCodeType;
import com.acme.application.shared.common.DateTimeUtility;
import com.acme.application.shared.document.DocumentTablePageData;
import com.acme.application.shared.document.DocumentTablePageData.DocumentTableRowData;
import com.acme.application.shared.document.IDocumentService;
import com.acme.application.shared.payment.PaymentFormData.DocumentTable;

public class DocumentService extends AbstractBaseService<Document, DocumentRecord> implements IDocumentService {

	@Override
	public Document getTable() {
		return Document.DOCUMENT;
	}

	@Override
	public Field<String> getIdColumn() {
		return Document.DOCUMENT.ID;
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(DocumentService.class);
	}

	@Override
	public BinaryResource getResource(String documentId) {
		DocumentRecord document = get(documentId);
		return new BinaryResource(document.getName(), document.getContent());
	}

	@Override
	public DocumentTablePageData getDocumentTableData(SearchFilter filter, String paymentId) {
		DocumentTablePageData pageData = new DocumentTablePageData();

		forEachDocumentWithPaymentId(paymentId, document -> {
			DocumentTableRowData row = pageData.addRow();
			row.setId(document.getId());
			row.setName(document.getName());
			row.setType(document.getType());
			row.setSize(document.getSize().toBigInteger());
			row.setUser(document.getUserId());
			row.setUploaded(DateTimeUtility.convertToDate(document.getUploaded()));
			row.setActive(document.getActive());
		});

		return pageData;
	}

	@Override
	public void store(String name, byte[] content, String userId, String paymentId) {
		String id = TableUtility.createId();
		String type = getDocumentType(name);
		BigDecimal size = BigDecimal.valueOf(content.length);
		String uploaded = DateTimeUtility.nowInUtcAsString();
		DocumentRecord document = new DocumentRecord(id, name, type, size, content, userId, uploaded, true);

		store(id, document);
		BEANS.get(PaymentDocumentService.class).store(paymentId, new PaymentDocumentRecord(paymentId, id));
	}

	private String getDocumentType(String name) {
        String type = IOUtility.getFileExtension(name);
        return BEANS.get(FileCodeType.class).getCode(type) != null ? type : FileCodeType.UknownCode.ID;
	}

	@Override
	public List<? extends ILookupRow<String>> getLookupRows(boolean activeOnly) {
		return getAll()
                .stream()
                .filter(document -> !activeOnly || (activeOnly && document.getActive()))
                .map(document -> new LookupRow<>(document.getId(), document.getName()))
                .collect(Collectors.toList());
	}

	@Override
	public DocumentTable getDocumentTableData(String paymentId) {
		DocumentTable table = new DocumentTable();

		forEachDocumentWithPaymentId(paymentId, document -> {
			com.acme.application.shared.payment.PaymentFormData.DocumentTable.DocumentTableRowData row = table.addRow();
			row.setId(document.getId());
			row.setName(document.getName());
			row.setType(document.getType());
			row.setSize(document.getSize().toBigInteger());
			row.setUser(document.getUserId());
			row.setUploaded(DateTimeUtility.convertToDate(document.getUploaded()));
			row.setActive(document.getActive());
		});

		return table;
	}

	private void forEachDocumentWithPaymentId(String paymentId, Consumer<? super DocumentRecord> consumer) {
		getContext()
        .selectFrom(getTable())
        .whereExists(
        		getContext()
        		.select(PaymentDocument.PAYMENT_DOCUMENT.DOCUMENT_ID)
        		.from(PaymentDocument.PAYMENT_DOCUMENT)
        		.where(PaymentDocument.PAYMENT_DOCUMENT.PAYMENT_ID.eq(paymentId)))
        .stream()
		.forEach(consumer);
	}
}
