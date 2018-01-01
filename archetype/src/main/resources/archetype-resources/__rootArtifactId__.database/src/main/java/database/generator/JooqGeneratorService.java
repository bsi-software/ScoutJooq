#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JooqGeneratorService {

	private static Logger LOG = LoggerFactory.getLogger(JooqGeneratorService.class);

	/**
	 * Returns a DSL context for the provided parameters.
	 * WARNING: Do not use this method in the application. It is intended for testing/debugging only. 
	 */
	public static DSLContext generateContext(String jdbcMappingName, SQLDialect dialect, String username, String password) {
		LOG.error("Method generateContext() is not intended for productive usage.");
		
		try {
			Connection connection;
			connection = DriverManager.getConnection(jdbcMappingName, username, password);
			Configuration configuration = new DefaultConfiguration();
			configuration.set(connection);
			configuration.set(dialect);
			
			return DSL.using(configuration);
		} 
		catch (SQLException e) {
			LOG.error("Failed to create connection. Exception: ", e);
			return null;
		}
	}
	
	public static DSLContext getContext(Connection connection, SQLDialect dialect) {
		Configuration configuration = new DefaultConfiguration();
		configuration.set(connection);
		configuration.set(dialect);
		return DSL.using(configuration);
	}
	
	public static Connection getConnection(String jdbcMappingName, String username, String password) throws SQLException {
		return DriverManager.getConnection(jdbcMappingName, username, password);
	}
}
