#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import java.sql.Connection;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.testing.platform.runner.PlatformTestRunner;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(PlatformTestRunner.class)
@RunWithSubject("admin")
//@Ignore
public class CreateTableStatementTest {

	@Test
	public void dumpCreateDatabaseStatements()  throws Exception {

		try (Connection connection = JooqGeneratorService
				.getConnection(GeneratorApplication.DB_MAPPING_NAME, "scout", "securePassw0rd")) 
		{
			DSLContext context = JooqGeneratorService
					.getContext(connection, GeneratorApplication.DB_DIALECT);

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
