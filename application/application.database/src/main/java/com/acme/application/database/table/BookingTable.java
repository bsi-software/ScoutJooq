package com.acme.application.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingTable extends AbstractCoreTable {

	public static final String TABLE = "BOOKING";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String DATE_FROM = "DATE_FROM";
	public static final String DATE_TO = "DATE_TO";
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
				.column(DESCRIPTION, TYPE_STRING_M)
				.column(DATE_FROM, TYPE_DATE)
				.column(DATE_TO, TYPE_DATE)
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
		return LoggerFactory.getLogger(BookingTable.class);
	}
}
