#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.jooq.DSLContext;

@ApplicationScoped
public class InitializerApplication {

	public static void main(String[] args) throws Exception {
		try (Connection connection = getConnection()) {
			initDatabase(connection);
		}
	}

	public static void initDatabase(Connection connection) {
		DSLContext context = getContext(connection);
		createDatabase(context);
		insertData(context);
	}

	public static Connection getConnection() throws SQLException {
		return JooqGeneratorService.getConnection(GeneratorApplication.DB_MAPPING_NAME, "scout", "securePassw0rd");
	}

	public static DSLContext getContext(Connection connection) {
		DSLContext context = JooqGeneratorService
				.getContext(connection, GeneratorApplication.DB_DIALECT);
		return context;
	}

	private static void createDatabase(DSLContext context) {
		createSchemas(context);		
		createTables(context);
	}

	private static void createTables(DSLContext context) {
		List<String> tables = DatabaseUtility.getTableNames(context);
		for (IGenerateTable table : BEANS.all(IGenerateTable.class)) {
			if(!tables.contains(table.getName())) {
				table.setContext(context);
				context.execute(table.getCreateSQL());
			}
		}
	}

	private static void createSchemas(DSLContext context) {
		List<String> schemas = DatabaseUtility.getSchemaNames(context);
		for (IDatabaseSchema schema : BEANS.all(IDatabaseSchema.class)) {
			if(!schemas.contains(schema.getName())) {
				schema.setContext(context);
				context.execute(schema.getCreateSQL());
			}
		}
	}	

	private static void insertData(DSLContext context) {
		BEANS.get(IDataInitializer.class).initialize(context);
		BEANS.get(IDataInitializer.class).addSamples(context);
	}
}
