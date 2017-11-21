#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server;

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
	public static final String KEY_SUPERUSER_SUBJECT = "${package}.server.superuser";
	
	public static final String KEY_AUTOCREATE = "${package}.server.sql.autocreate";
	public static final String KEY_AUTOPOPULATE = "${package}.server.sql.autopopulate";
	
	public static final String KEY_DRIVER = "${package}.server.sql.driver";
	public static final String KEY_DIALECT = "${package}.server.sql.dialect";
	public static final String KEY_MAPPING = "${package}.server.sql.jdbc.mapping.name";
	public static final String KEY_USERNAME = "${package}.server.sql.username";
	public static final String KEY_PASSWORD = "${package}.server.sql.password";

	// server property defaults
	public static final String DEFAULT_SUPERUSER = ServerSession.SUPER_USER_ID;

	public static final boolean DEFAULT_AUTOCREATE = true;
	public static final boolean DEFAULT_AUTOPOPULATE = true;

	public static final String DEFAULT_DRIVER = "org.postgresql.Driver";
	public static final SQLDialect DEFAULT_DIALECT = SQLDialect.POSTGRES_9_5;
	
	// TODO update DEFAULT_HOST according to your setup
	public static final String DEFAULT_HOST = null;
	public static final String GENERIC_HOST = "<DB-HOST-ADDRESS>";
	public static final String DEFAULT_MAPPING = "jdbc:postgresql://" + ObjectUtility.nvl(DEFAULT_HOST, GENERIC_HOST) + ":5432/postgres";
	public static final String DEFAULT_USERNAME = "postgres";
	public static final String DEFAULT_PASSWORD = "securePassw0rd";
	

	public static class DriverProperty extends AbstractStringConfigProperty {

		@Override
		protected String getDefaultValue() {
			return DEFAULT_DRIVER;
		}

		@Override
		public String getKey() {
			return KEY_DRIVER;
		}
	}
	
	public static class DialectProperty extends AbstractConfigProperty<SQLDialect, String> {

		@Override
		protected SQLDialect getDefaultValue() {
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
	}

	public static class DatabaseAutoCreateProperty extends AbstractBooleanConfigProperty {

		@Override
		protected Boolean getDefaultValue() {
			return DEFAULT_AUTOCREATE;
		}

		@Override
		public String getKey() {
			return KEY_AUTOCREATE;
		}
	}

	public static class DatabaseAutoPopulateProperty extends AbstractBooleanConfigProperty {

		@Override
		protected Boolean getDefaultValue() {
			return DEFAULT_AUTOPOPULATE;
		}

		@Override
		public String getKey() {
			return KEY_AUTOPOPULATE;
		}
	}

	public static class JdbcMappingNameProperty extends AbstractStringConfigProperty {

		@Override
		protected String getDefaultValue() {
			if(DEFAULT_MAPPING.contains(GENERIC_HOST)) {
			    System.err.println("Invalid default database mapping: '" + DEFAULT_MAPPING 
			    		+ "'. Exiting. Update class ServerProperties or provide a valid value for '"
			    		+ KEY_MAPPING 
			    		+"' in the properties file");
			    System.exit(-1);
			}
			
			return DEFAULT_MAPPING;
		}

		@Override
		public String getKey() {
			return KEY_MAPPING;
		}
	}

	public static class UsernameProperty extends AbstractStringConfigProperty {

		@Override
		protected String getDefaultValue() {
			return DEFAULT_USERNAME;
		}

		@Override
		public String getKey() {
			return KEY_USERNAME;
		}
	}

	public static class PasswordProperty extends AbstractStringConfigProperty {

		@Override
		protected String getDefaultValue() {
			return DEFAULT_PASSWORD;
		}

		@Override
		public String getKey() {
			return KEY_PASSWORD;
		}
	}

	public static class SuperUserSubjectProperty extends AbstractSubjectConfigProperty {

		@Override
		protected Subject getDefaultValue() {
			return convertToSubject(DEFAULT_SUPERUSER);
		}

		@Override
		public String getKey() {
			return KEY_SUPERUSER_SUBJECT;
		}
	}
}
