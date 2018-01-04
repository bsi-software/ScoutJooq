#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.sql;

import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.server.jdbc.derby.AbstractDerbySqlService;

import ${package}.server.ServerProperties;

public class SqlService extends AbstractDerbySqlService {

	// TODO inherit proper subclass depending on db vendor

	@Override
	protected String getConfiguredJdbcDriverName() {
		return CONFIG.getPropertyValue(ServerProperties.DriverProperty.class);
	}

	@Override
	protected String getConfiguredUsername() {
		return CONFIG.getPropertyValue(ServerProperties.UsernameProperty.class);
	}

	@Override
	protected String getConfiguredPassword() {
		return CONFIG.getPropertyValue(ServerProperties.PasswordProperty.class);
	}

	@Override
	protected String getConfiguredJdbcMappingName() {
		return CONFIG.getPropertyValue(ServerProperties.JdbcMappingNameProperty.class);
	}
}
