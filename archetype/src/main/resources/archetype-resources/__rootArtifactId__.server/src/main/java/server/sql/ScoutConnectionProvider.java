#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.sql;

import java.sql.Connection;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.server.jdbc.ISqlService;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

public class ScoutConnectionProvider implements ConnectionProvider {

	private ISqlService service;

	public ScoutConnectionProvider() {
		this.service = BEANS.get(ISqlService.class);
	}

	@Override
	public Connection acquire() throws DataAccessException {
		return service.getConnection();
	}

	@Override
	public void release(Connection connection) throws DataAccessException {
		// NOP
	}

}
