package com.acme.application.database.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.generator.AbstractSchema;

public class CoreSchema extends AbstractSchema {

	public static final String SCHEMA = "core";

	@Override
	public String getName() {
		return SCHEMA;
	}

	public Logger getLogger() {
		return LoggerFactory.getLogger(CoreSchema.class);
	}
}
