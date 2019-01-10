#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
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
		return JooqGeneratorService.getConnection(GeneratorApplication.DB_MAPPING_NAME, GeneratorApplication.DB_USER, GeneratorApplication.DB_PASSWORD);
	}

	public static DSLContext getContext(Connection connection) {
		return JooqGeneratorService.getContext(connection, GeneratorApplication.DB_DIALECT);
	}

	private static void createDatabase(DSLContext context) {
		createDatabaseObjectsinContext(DatabaseUtility.getTableNames(context), BEANS.all(IGenerateTable.class), context);
		createDatabaseObjectsinContext(DatabaseUtility.getSchemaNames(context), BEANS.all(IDatabaseSchema.class), context);
	}

	private static void createDatabaseObjectsinContext(List<String> created, List<? extends IDatabaseObject> databaseObjects, DSLContext context) {
		databaseObjects
		.stream()
		.filter(dbObject -> !created.contains(dbObject.getName()))
		.forEach(dbObject -> {
			dbObject.setContext(context);
			context.execute(dbObject.getCreateSQL());
		});
	}

	private static void insertData(DSLContext context) {
		BEANS.get(IDataInitializer.class).initialize(context);
		BEANS.get(IDataInitializer.class).addSamples(context);
	}
}
