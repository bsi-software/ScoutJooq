package com.acme.application.database.table;

import com.acme.application.database.generator.AbstractTable;

public abstract class AbstractCoreTable extends AbstractTable {

	@Override
	public String getSchemaName() {
		return CoreSchema.SCHEMA;
	}
}
