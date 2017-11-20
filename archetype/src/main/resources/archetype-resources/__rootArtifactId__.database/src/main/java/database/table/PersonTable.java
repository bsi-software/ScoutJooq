#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonTable extends AbstractCoreTable {

	public static String TABLE = "PERSON";
	public static String FIRST_NAME = "FIRST_NAME";
	public static String LAST_NAME = "LAST_NAME";
	public static String SEX = "SEX";

	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(ID, TYPE_ID)
				.column(FIRST_NAME, TYPE_STRING_S)
				.column(LAST_NAME, TYPE_STRING_S)
				.column(SEX, TYPE_CODE)
				.column(ACTIVE, TYPE_BOOLEAN)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(ID))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(PersonTable.class);
	}
}
