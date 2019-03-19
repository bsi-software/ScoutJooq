package com.acme.application.database.generator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Schema;
import org.jooq.Table;

public class DatabaseUtility {

	public static List<String> getSchemaNames(DSLContext context) {
		return getNames(context.meta().getSchemas(), Schema::getName);
	}

	public static List<String> getTableNames(DSLContext context) {
		return getNames(context.meta().getTables(), Table::getName);
	}

	private static <T> List<String> getNames(List<? extends T> databaseObjects, Function<? super T, String> getterName) {
		return databaseObjects.stream()
				.map(getterName)
				.collect(Collectors.toList());
	}
}
