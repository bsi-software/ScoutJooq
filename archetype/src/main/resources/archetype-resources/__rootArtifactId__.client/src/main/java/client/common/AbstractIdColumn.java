#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.common;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;

public class AbstractIdColumn extends AbstractStringColumn {

	@Override
	protected boolean getConfiguredPrimaryKey() {
		return true;
	}
	
	@Override
	protected boolean getConfiguredDisplayable() {
		return false;
	}
}
