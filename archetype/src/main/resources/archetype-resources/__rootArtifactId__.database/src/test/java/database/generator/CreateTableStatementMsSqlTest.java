#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import java.sql.Connection;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.testing.platform.runner.PlatformTestRunner;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(PlatformTestRunner.class)
@RunWithSubject("admin")
public class CreateTableStatementMsSqlTest {

	@Test
	public void dumpCreateTableStatements()  throws Exception {

		try (Connection connection = JooqGeneratorService
				.getConnection(
						"jdbc:postgresql://192.168.99.100:5432/SCOUT", 
						"admin", 
						"password")) 
		{
			DSLContext context = JooqGeneratorService
					.getContext(connection, SQLDialect.POSTGRES);

			for (IDatabaseSchema schema : BEANS.all(IDatabaseSchema.class)) {
				schema.setContext(context);
				System.out.println(schema.getCreateSQL());
			}
			
			for (IGenerateTable table : BEANS.all(IGenerateTable.class)) {
				table.setContext(context);
				System.out.println(table.getCreateSQL());
			}			
		}
	}
}
