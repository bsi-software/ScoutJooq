package com.acme.application.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRoleTable extends AbstractCoreTable {

	public static String TABLE = "USER_ROLE";
	public static String ROLE_NAME = RoleTable.TABLE + "_NAME";

	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(UserTable.USERNAME, TYPE_STRING_S.nullable(false))
				.column(ROLE_NAME, TYPE_ID)
				.constraints(
						DSL.constraint(getPKName()).primaryKey(
								UserTable.USERNAME, 
								ROLE_NAME)
						)
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(UserRoleTable.class);
	}
}