#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeTable extends AbstractCoreTable {

	public static final String TABLE = "TYPE";
	public static final String ID = "ID";
	public static final String TYPE = "CODE_TYPE";

	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(ID, TYPE_ID)
				.column(TYPE, TYPE_CODE)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(ID))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(TypeTable.class);
	}
}