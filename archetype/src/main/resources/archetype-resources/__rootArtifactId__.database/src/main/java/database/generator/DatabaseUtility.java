#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;

public class DatabaseUtility {

	public static List<String> getSchemaNames(DSLContext context) {
		return context
				.meta()
				.getSchemas()
				.stream()
				.map(schema -> { return schema.getName(); })
				.collect(Collectors.toList());
	}

	public static List<String> getTableNames(DSLContext context) {
		return context
				.meta()
				.getTables()
				.stream()
				.map(table -> { return table.getName(); })
				.collect(Collectors.toList());
	}
}
