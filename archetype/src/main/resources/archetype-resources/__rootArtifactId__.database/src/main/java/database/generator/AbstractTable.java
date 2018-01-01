#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import org.eclipse.scout.rt.platform.util.StringUtility;
import org.jooq.impl.DSL;

public abstract class AbstractTable extends AbstractDatabaseObject implements IGenerateTable {

	public static String PRIMARY_KEY_PREFIX = "PK_";

	@Override
	public String getSchemaName() {
		return "";
	}

	@Override
	public void create() {
		getLogger().info("SQL-DEV create table: {}", getName());
		super.create();
	}

	@Override
	public void drop() {
		getLogger().info("SQL-DEV drop table: {}", getName());

		boolean exists = getContext().fetchExists(DSL.table(DSL.name(getName())));

		if (exists) {
			getContext().dropTable(getName()).execute();
		}
	}

	protected String getPKName() {
		return PRIMARY_KEY_PREFIX + getName();
	}

	@Override
	public String getCreateSQL() {
		return postProcessForSchema(createSQLInternal());
	}

	// FIXME there has to be a better way!
	private String postProcessForSchema(String sql) {
		if (!StringUtility.hasText(sql)) {
			return null;
		}

		if (!StringUtility.hasText(getSchemaName())) {
			return sql;
		}

		// SQLDialect dialect = getContext().dialect();
		String schema = getSchemaName();
		String sqlLC = sql.toLowerCase();

		if (!sqlLC.startsWith("create table")) {
			return sql;
		}

		// if (dialect.equals(SQLDialect.SQLSERVER)) {
		// if (!sqlLC.startsWith("create table [" + schema + "].")) {
		// return String.format("create table [%s].%s", schema, sql.substring(13));
		// }
		// } else {
		if (!sqlLC.startsWith("create table ${symbol_escape}"" + schema + "${symbol_escape}".")) {
			return String.format("create table ${symbol_escape}"%s${symbol_escape}".%s", schema, sql.substring(13));
		}
		// }

		return sql;
	}

}
