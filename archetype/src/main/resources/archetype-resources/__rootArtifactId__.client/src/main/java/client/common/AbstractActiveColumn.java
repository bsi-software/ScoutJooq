#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.common;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.shared.TEXTS;

public class AbstractActiveColumn extends AbstractBooleanColumn {
	@Override
	protected String getConfiguredHeaderText() {
		return TEXTS.get("Active");
	}

	@Override
	protected int getConfiguredWidth() {
		return 100;
	}
}
