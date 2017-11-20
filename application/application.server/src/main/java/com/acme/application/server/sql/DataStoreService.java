package com.acme.application.server.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.generator.GeneratorApplication;
import com.acme.application.database.table.TableDataInitializer;
import com.acme.application.server.ServerProperties;

public class DataStoreService extends JooqService implements IDataStoreService {

	private static final Logger LOG = LoggerFactory.getLogger(DataStoreService.class);

	@Override
	public void create() {
		createDatabaseScheme();
		populateDatabase();
	}

	private void createDatabaseScheme() {
		if(CONFIG.getPropertyValue(ServerProperties.DatabaseAutoCreateProperty.class)) {
			LOG.info("Create database schema");

			try(Connection connection = getConnection()) {
				GeneratorApplication.setupDatabase(getContext(connection));
			}
			catch (SQLException e) {
				LOG.error("Failed to execute createDatabaseScheme(). exception: ", e);
			}
		}
	}

	private void populateDatabase() {
		if(CONFIG.getPropertyValue(ServerProperties.DatabaseAutoPopulateProperty.class)) {
			LOG.info("Populate database");

			try(Connection connection = getConnection()) {
				TableDataInitializer data = BEANS.get(TableDataInitializer.class);
				DSLContext context = getContext(connection);
				data.initialize(context);

				if(CONFIG.getPropertyValue(ServerProperties.DatabaseAutoPopulateProperty.class)) {
					LOG.info("Add additional sample data");
					data.addSamples(context);
				}
			}
			catch (SQLException e) {
				LOG.error("Failed to execute populateDatabase(). exception: ", e);
			}
		}
	}

	@Override
	public void drop() {
		LOG.error("Add implementation");
	}
}
