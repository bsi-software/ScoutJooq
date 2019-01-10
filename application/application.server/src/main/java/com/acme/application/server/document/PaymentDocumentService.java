package com.acme.application.server.document;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.service.IService;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.or.core.tables.PaymentDocument;
import com.acme.application.database.or.core.tables.records.PaymentDocumentRecord;
import com.acme.application.server.common.AbstractBaseService;
import com.acme.application.shared.document.IDocumentService;

public class PaymentDocumentService  extends AbstractBaseService<PaymentDocument, PaymentDocumentRecord> implements IService{

	@Override
	public PaymentDocument getTable() {
		return PaymentDocument.PAYMENT_DOCUMENT;
	}

	@Override
	public Field<String> getIdColumn() {
		return PaymentDocument.PAYMENT_DOCUMENT.PAYMENT_ID;
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(PaymentDocumentService.class);
	}

	@Override
	public int delete(String id) {
		List<PaymentDocumentRecord> records = getContext()
				.selectFrom(getTable())
				.where(getIdColumn().eq(id))
				.fetchStream()
				.collect(Collectors.toList());
		int delete = super.delete(id);
		if (delete > 0) {
			IDocumentService service = BEANS.get(IDocumentService.class);
			records
			.stream()
			.map(PaymentDocumentRecord::getDocumentId)
			.forEach(service::delete);
		}
		return delete;
	}

}
