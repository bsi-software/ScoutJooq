#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentTable extends AbstractCoreTable {

	public static final String TABLE = "PAYMENT";
	public static final String AMOUNT = "AMOUNT";
	public static final String USAGE = "USAGE";
	public static final String SHARED = "SHARED";
	public static final String DATE = "DATE";
	public static final String NOTE = "NOTE";
	public static final String USER_ID = "USER_ID";

	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(ID, TYPE_ID)
				.column(AMOUNT, TYPE_STRING_M)
				.column(USAGE, TYPE_STRING_M)
				.column(SHARED, TYPE_BOOLEAN)
				.column(DATE, TYPE_DATE)
				.column(NOTE, TYPE_STRING_XL)
				// foreign key to user
				.column(USER_ID, TYPE_ID_OPTIONAL)
				.column(ACTIVE, TYPE_BOOLEAN)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(ID))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(PaymentTable.class);
	}
}
