#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.database.generator.AbstractSchema;

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
