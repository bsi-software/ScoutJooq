package com.acme.application.server;

import java.util.Arrays;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.config.AbstractBooleanConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractSubjectConfigProperty;
import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.util.ObjectUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.jooq.SQLDialect;

public class ServerProperties {
	
	// server property keys
	public static final String KEY_SUPERUSER_SUBJECT = "com.acme.application.server.superuser";
	
	public static final String KEY_AUTOCREATE = "com.acme.application.server.sql.autocreate";
	public static final String KEY_AUTOPOPULATE = "com.acme.application.server.sql.autopopulate";
	
	public static final String KEY_DRIVER = "com.acme.application.server.sql.driver";
	public static final String KEY_DIALECT = "com.acme.application.server.sql.dialect";
	public static final String KEY_MAPPING = "com.acme.application.server.sql.jdbc.mapping.name";
	public static final String KEY_USERNAME = "com.acme.application.server.sql.username";
	public static final String KEY_PASSWORD = "com.acme.application.server.sql.password";

	// server property defaults
	public static final String DEFAULT_SUPERUSER = ServerSession.SUPER_USER_ID;

	public static final boolean DEFAULT_AUTOCREATE = true;
	public static final boolean DEFAULT_AUTOPOPULATE = true;

	public static final String DEFAULT_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final SQLDialect DEFAULT_DIALECT = SQLDialect.DERBY;
	public static final String DEFAULT_MAPPING = "jdbc:sqlserver://192.168.99.100:1433;DatabaseName=SCOUT";
	public static final String DEFAULT_USERNAME = "SA";
	public static final String DEFAULT_PASSWORD = "<YourStrong!Passw0rd>";
	

	public static class DriverProperty extends AbstractStringConfigProperty {

		@Override
		public String getDefaultValue() {
			return DEFAULT_DRIVER;
		}

		@Override
		public String getKey() {
			return KEY_DRIVER;
		}

		@Override
		public String description() {
			return "Classname of the JDBC driver";
		}
	}
	
	public static class DialectProperty extends AbstractConfigProperty<SQLDialect, String> {

		@Override
		public SQLDialect getDefaultValue() {
			return DEFAULT_DIALECT;
		}

		@Override
		public String getKey() {
			return KEY_DIALECT;
		}

		@Override
		protected SQLDialect parse(String value) {
			String dialect = ObjectUtility.nvl(value, "");
			try {
				return SQLDialect.valueOf(dialect);
			}
			catch (Exception e) {
			    throw new PlatformException("Invalid SQL dialect '" + dialect + "' for property '" + getKey() 
			    + "'. Valid names are " + getValidValues());
			}
		}
		
		private String getValidValues() {
			return "'" + StringUtility.join("','", Arrays.asList(SQLDialect.values())) + "'";
		}

		@Override
		public String description() {
			return "Sql dialect of the database";
		}
	}

	public static class DatabaseAutoCreateProperty extends AbstractBooleanConfigProperty {

		@Override
		public Boolean getDefaultValue() {
			return DEFAULT_AUTOCREATE;
		}

		@Override
		public String getKey() {
			return KEY_AUTOCREATE;
		}

		@Override
		public String description() {
		return "Boolean property to create database if there is none";
		}
	}

	public static class DatabaseAutoPopulateProperty extends AbstractBooleanConfigProperty {

		@Override
		public Boolean getDefaultValue() {
			return DEFAULT_AUTOPOPULATE;
		}

		@Override
		public String getKey() {
			return KEY_AUTOPOPULATE;
		}

		@Override
		public String description() {
			return "Boolean property to populate database automatically";
		}
	}

	public static class JdbcMappingNameProperty extends AbstractStringConfigProperty {

		@Override
		public String getDefaultValue() {
			return DEFAULT_MAPPING;
		}

		@Override
		public String getKey() {
			return KEY_MAPPING;
		}

		@Override
		public String description() {
			return "JDBC Url of database";
		}
	}

	public static class UsernameProperty extends AbstractStringConfigProperty {

		@Override
		public String getDefaultValue() {
			return DEFAULT_USERNAME;
		}

		@Override
		public String getKey() {
			return KEY_USERNAME;
		}

		@Override
		public String description() {
			return "Username for JDBC Connection";
		}
	}

	public static class PasswordProperty extends AbstractStringConfigProperty {

		@Override
		public String getDefaultValue() {
			return DEFAULT_PASSWORD;
		}

		@Override
		public String getKey() {
			return KEY_PASSWORD;
		}

		@Override
		public String description() {
			return "Password for JDBC Connection";
		}
	}

	public static class SuperUserSubjectProperty extends AbstractSubjectConfigProperty {

		@Override
		public Subject getDefaultValue() {
			return convertToSubject(DEFAULT_SUPERUSER);
		}

		@Override
		public String getKey() {
			return KEY_SUPERUSER_SUBJECT;
		}

		@Override
		public String description() {
			return "Username of privileged user in Servlet Container";
		}
	}
}
