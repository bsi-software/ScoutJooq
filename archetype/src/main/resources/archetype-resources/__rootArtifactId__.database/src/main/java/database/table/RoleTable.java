#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleTable extends AbstractCoreTable {

	public static final String TABLE = "ROLE";
	public static final String NAME = "NAME";
	public static final String TEXT = "TEXT";

	public static final String TEXT_PREFIX = "role:";
			
	public static final String ROOT = "root";
	public static final String USER = "user";
	public static final String GUEST = "guest";
	
	public static String toTextKey(String roleName) {
		return String.format("%s%s", TEXT_PREFIX, roleName);
	}

	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(NAME, TYPE_ID)
				.column(TEXT, TYPE_ID)
				.column(ACTIVE, TYPE_BOOLEAN)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(NAME))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(RoleTable.class);
	}
}
