#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import ${package}.server.ServerSession;

@RunWithSubject("root")
@RunWith(ServerTestRunner.class)
@RunWithServerSession(ServerSession.class)
public class JooqServiceTest {

	@Test
	public void testGetConnection() throws SQLException {
		Connection connection = BEANS.get(JooqService.class).getConnection();
		assertNotNull("Connection is null", connection);
		assertTrue("Connection is not valid", connection.isValid(5));
		assertFalse("Connection is closed (unexpectedly)", connection.isClosed());
		
		connection.close();
		
		assertFalse("Connection is still valid", connection.isValid(5));
		assertTrue("Connection is open (unexpectedly)", connection.isClosed());
	}
}
