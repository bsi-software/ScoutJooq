package com.acme.application.server.payment;

import java.math.BigDecimal;
import java.util.Date;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.or.core.tables.Payment;
import com.acme.application.database.or.core.tables.records.PaymentRecord;
import com.acme.application.database.table.TableUtility;
import com.acme.application.server.ServerSession;
import com.acme.application.server.common.AbstractBaseService;
import com.acme.application.server.document.PaymentDocumentService;
import com.acme.application.shared.document.IDocumentService;
import com.acme.application.shared.payment.IPaymentService;
import com.acme.application.shared.payment.PaymentFormData;
import com.acme.application.shared.payment.PaymentTablePageData;
import com.acme.application.shared.payment.PaymentTablePageData.PaymentTableRowData;

public class PaymentService extends AbstractBaseService<Payment, PaymentRecord>  implements IPaymentService {

	@Override
	public Payment getTable() {
		return Payment.PAYMENT;
	}

	@Override
	public Field<String> getIdColumn() {
		return Payment.PAYMENT.ID;
	}


	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(PaymentService.class);
	}


	@Override
	public PaymentTablePageData getPaymentTableData(SearchFilter filter) {
		PaymentTablePageData pageData = new PaymentTablePageData();

		getAll()
		.stream()
		.forEach(payment -> recordToRowData(pageData.addRow(), payment));

		return pageData;
	}

	@Override
	public PaymentFormData load(PaymentFormData formData) {
		PaymentRecord payment = getOrCreate(formData.getPaymentId().getValue());
        recordToFormData(formData, payment);
        formData.getDocumentTable().setRows(BEANS.get(IDocumentService.class).getDocumentTableData(payment.getId()).getRows());
        return formData;
	}

	@Override
	public PaymentFormData store(PaymentFormData formData) {
		String paymentId = formData.getPaymentId().getValue();
		PaymentRecord payment = getOrCreate(paymentId);
		formDataToRecord(formData, payment);
        store(payment.getId(), payment);
        return formData;
	}

	@Override
	public int delete(String id) {
		int delete = super.delete(id);
		if (delete > 0) {
			BEANS.get(PaymentDocumentService.class).delete(id);
		}
		return delete;
	}


	public PaymentRecord getOrCreate(String paymentId) {
		PaymentRecord payment = get(paymentId);

        if (payment != null) {
            return payment;
        }

        payment = new PaymentRecord();
        payment.setId(TableUtility.createId());
        payment.setActive(true);
        payment.setDate(new Date());
        payment.setShared(true);
        payment.setUserId(ServerSession.get().getUserId());

        return payment;
    }

	private void formDataToRecord(PaymentFormData formData, PaymentRecord payment) {
		if (payment != null && formData != null) {
			payment.setUsage(formData.getUsage().getValue());
			payment.setDate(formData.getDate().getValue());
			payment.setShared(formData.getShared().getValue());
			payment.setAmount(formData.getAmount().getValue().toString());
			payment.setNote(formData.getNote().getValue());
			payment.setUserId(formData.getUserId().getValue());
			payment.setActive(formData.getActive().getValue());
		}
	}

	private void recordToFormData(PaymentFormData formData, PaymentRecord payment) {
		if (payment != null && formData != null) {
			formData.getPaymentId().setValue(payment.getId());
			formData.getUsage().setValue(payment.getUsage());
			formData.getDate().setValue(payment.getDate());
			formData.getShared().setValue(payment.getShared());
			String amount = payment.getAmount();
			if (amount != null) {
				formData.getAmount().setValue(new BigDecimal(amount));
			}
			formData.getNote().setValue(payment.getNote());
			formData.getUserId().setValue(payment.getUserId());
			formData.getActive().setValue(payment.getActive());
		}
	}

	private void recordToRowData(PaymentTableRowData row, PaymentRecord payment) {
		row.setId(payment.getId());
		row.setUsage(payment.getUsage());
		row.setDate(payment.getDate());
		row.setShared(payment.getShared());
		row.setAmount(new BigDecimal(payment.getAmount()));
		row.setNote(payment.getNote());
		row.setUser(payment.getUserId());
		row.setActive(payment.getActive());
	}

}
