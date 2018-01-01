#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RolePermissionTable extends AbstractCoreTable {

	public static String TABLE = "ROLE_PERMISSION";
	public static String ROLE_NAME = RoleTable.TABLE + "_NAME";
	public static String PERMISSION = "PERMISSION";

	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(ROLE_NAME, TYPE_ID)
				.column(PERMISSION, TYPE_STRING_M.nullable(false))
				.constraints(
						DSL.constraint(getPKName()).primaryKey(
								ROLE_NAME,
								PERMISSION)
						)
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(RolePermissionTable.class);
	}
}
