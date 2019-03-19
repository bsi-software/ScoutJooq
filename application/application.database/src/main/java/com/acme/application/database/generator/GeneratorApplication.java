package com.acme.application.database.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.CustomType;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.ForcedType;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class GeneratorApplication {

	private static final Logger LOG = LoggerFactory.getLogger(GeneratorApplication.class);

	public static final String OUTPUT_DIRECTORY = "src/generated/java";
	public static final String OUTPUT_PACKAGE = "com.acme.application.database.or";

	//TODO adapt to your set up
	public static final String DB_USER = "postgres";
	public static final String DB_PASSWORD = "securePassw0rd";
	public static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String DB_MAPPING_NAME = "jdbc:derby:memory:SCOUT;create=true";
	public static final String DB_JOOQ_NAME = "org.jooq.util.derby.DerbyDatabase";
	public static final SQLDialect DB_DIALECT = SQLDialect.DERBY;

	// TODO ask how to do this for uuid, does not seem to work (using workaround with varchar(46) for id columns now
	private static final String CONVERTER_DATE = "com.acme.application.database.generator.converter.DateConverter";
	private static final String CONVERTER_TIMESTAMP = "com.acme.application.database.generator.converter.TimeStampConverter";
	private static final String CONVERTER_LONG = "com.acme.application.database.generator.converter.LongConverter";
	private static final String CONVERTER_UUID = "com.acme.application.database.generator.converter.UUIDConverter";

	public static void main(String[] args) {
		try {
			Configuration configuration = new Configuration()
					.withGenerator(
							new Generator()
							.withName("org.jooq.util.JavaGenerator")
							.withDatabase(
									new Database()
									.withCustomTypes(
											new CustomType()
											.withConverter(CONVERTER_UUID)
											.withName("java.util.UUID"),
											new CustomType()
											.withConverter(CONVERTER_DATE)
											.withName("java.util.Date"),
											new CustomType()
											.withConverter(CONVERTER_LONG)
											.withName("java.math.BigDecimal")
											)
									.withForcedTypes(
											new ForcedType()
											.withName("java.util.UUID")
											.withTypes("varchar(36)"),
											new ForcedType()
											.withName("java.util.Date")
											.withTypes("date"),
											new ForcedType()
											.withName("java.math.BigDecimal")
											.withTypes("bigint"),
											new ForcedType()
											.withUserType("java.util.Date")
											.withConverter(CONVERTER_TIMESTAMP)
											.withTypes("timestamp")
											)
									.withName(DB_JOOQ_NAME)
									.withIncludes(".*")
									.withExcludes("SYS.*|SYSIBM.*"))
							.withTarget(
									new Target()
									.withDirectory(OUTPUT_DIRECTORY)
									.withPackageName(OUTPUT_PACKAGE)));
			new GeneratorTool().run(configuration);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param connection
	 * @throws SQLException
	 */
	public static void setupDatabase(DSLContext context) {

		try {
			// create database schemas
			List<String> schemas = DatabaseUtility.getSchemaNames(context);
			for (IDatabaseSchema schema : BEANS.all(IDatabaseSchema.class)) {
				createDatabaseObject(context, schemas, schema);
			}

			// create database tables
			List<String> tables = DatabaseUtility.getTableNames(context);
			for (IGenerateTable table : BEANS.all(IGenerateTable.class)) {
				createDatabaseObject(context, tables, table);
			}
		}
		catch (Exception e) {
			LOG.error("Failed to create database object: {}", e.getMessage());
			throw new ProcessingException("Failed to create database object", e);
		}
	}

	private static void createDatabaseObject(DSLContext context, List<String> objects,
			IDatabaseObject object) throws SQLException
	{
		String name = object.getName();

		if(!objects.contains(name)) {
			object.setContext(context);
			String sql = object.getCreateSQL();
			context.execute(sql);

			object.getLogger().info("Database object {} successfully created", name);
		}
		else {
			object.getLogger().warn("Database object {} already exists, nothing created", name);
		}
	}

	public static void teardownDatabase(DSLContext context) {
		for (IGenerateTable table : BEANS.all(IGenerateTable.class)) {
			table.setContext(context);
			table.drop();
		}
	}

	public static class GeneratorTool extends GenerationTool {

		@Override
		public void run(Configuration jaxbConfiguration) throws Exception {
			try (Connection connection = DriverManager.getConnection(DB_MAPPING_NAME, DB_USER, DB_PASSWORD))  {
				GeneratorApplication.setupDatabase(new DefaultDSLContext(connection, DB_DIALECT));
				setConnection(connection);
				super.run(jaxbConfiguration);
			}
		}
	}
}
