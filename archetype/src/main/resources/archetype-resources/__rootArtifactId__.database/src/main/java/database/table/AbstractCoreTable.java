#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import ${package}.database.generator.AbstractTable;

public abstract class AbstractCoreTable extends AbstractTable {

	@Override
	public String getSchemaName() {
		return CoreSchema.SCHEMA;
	}
}
