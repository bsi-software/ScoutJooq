package com.acme.application.database.generator;

public abstract class AbstractSchema extends AbstractDatabaseObject implements IDatabaseSchema {

	@Override
	public void create() {
		getLogger().info("SQL-DEV create schema: {}", getName());
		super.create();
	}

	@Override
	public void drop() {

	}
}
