#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server.testing;

import org.eclipse.scout.rt.platform.Replace;

import ${package}.server.sql.DatabaseSetupService;


@Replace
public class TestDatabaseSetupService extends DatabaseSetupService {
	public void autoCreateDatabase() {
		//Dont do anything, this is just tests
	}
}
