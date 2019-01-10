#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentDocumentTable extends AbstractCoreTable {

	public static final String TABLE = "PAYMENT_DOCUMENT";
	public static final String PAYMENT_ID = "PAYMENT_ID";
	public static final String DOCUMENT_ID = "DOCUMENT_ID";


	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(PAYMENT_ID, TYPE_ID)
				.column(DOCUMENT_ID, TYPE_ID)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(PAYMENT_ID))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(PaymentDocumentTable.class);
	}

}
