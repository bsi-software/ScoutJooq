#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.user;

import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import ${package}.server.ServerSession;

@RunWithSubject("root")
@RunWith(ServerTestRunner.class)
@RunWithServerSession(ServerSession.class)
public class UserServiceTest {

	@Test
	public void dummyTest() {
		// TODO [mzi] add test case		
	}
}
