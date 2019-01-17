package com.acme.application.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingDocumentTable extends AbstractCoreTable {

	public static final String TABLE = "BOOKING_DOCUMENT";
	public static final String BOOKING_ID = "BOOKING_ID";
	public static final String DOCUMENT_ID = "DOCUMENT_ID";


	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(BOOKING_ID, TYPE_ID)
				.column(DOCUMENT_ID, TYPE_ID)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(BOOKING_ID))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(BookingDocumentTable.class);
	}

}
