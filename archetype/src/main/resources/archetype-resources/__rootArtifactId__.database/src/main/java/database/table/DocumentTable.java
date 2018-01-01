#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentTable extends AbstractCoreTable {

	public static final String TABLE = "DOCUMENT";
	public static final String NAME = "NAME";
	public static final String SIZE = "SIZE";
	public static final String TYPE = "TYPE";
	public static final String CONTENT = "CONTENT";
	public static final String UPLOADED = "UPLOADED";
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
				.column(NAME, TYPE_STRING_M)
				.column(TYPE, TYPE_STRING_XS)
				.column(SIZE, TYPE_SIZE)
				.column(CONTENT, TYPE_BLOB)
				// foreign key to user
				.column(USER_ID, TYPE_ID)
				.column(UPLOADED, TYPE_TIMESTAMP)
				.column(ACTIVE, TYPE_BOOLEAN)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(ID))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(DocumentTable.class);
	}
}
