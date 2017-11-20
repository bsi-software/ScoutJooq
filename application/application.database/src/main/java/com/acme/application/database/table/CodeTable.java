package com.acme.application.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeTable extends AbstractCoreTable {

	public static final String TABLE = "CODE";
	public static final String ID = "ID";
	public static final String TYPE = "TYPE_ID";
	public static final String ORDER = "ORDER";
	public static final String ICON = "ICON";
	public static final String VALUE = "VALUE";


	@Override
	public String getName() {
		return TABLE;
	}
	
	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(ID, TYPE_ID)
				.column(TYPE, TYPE_ID)
				.column(ORDER, TYPE_DOUBLE)
				.column(ICON, TYPE_STRING_S)
				.column(VALUE, TYPE_STRING_S)
				.column(ACTIVE, TYPE_BOOLEAN)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(ID, TYPE))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(CodeTable.class);
	}
}