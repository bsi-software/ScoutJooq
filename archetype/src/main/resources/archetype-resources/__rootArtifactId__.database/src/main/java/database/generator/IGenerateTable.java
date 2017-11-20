#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import java.sql.Date;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.SQLDataType;
import org.jooq.types.UInteger;

public interface IGenerateTable extends IDatabaseObject {

	public static final String ID = "ID";
	public static final DataType<String> TYPE_ID = SQLDataType.VARCHAR.length(36).nullable(false);
	public static final DataType<String> TYPE_ID_OPTIONAL = SQLDataType.VARCHAR.length(36).nullable(true);
	
	public static final String ACTIVE = "ACTIVE";
	public static final DataType<Boolean> TYPE_BOOLEAN = SQLDataType.BOOLEAN;
	public static final DataType<Integer> TYPE_INTEGER = SQLDataType.INTEGER;
	
	public static final DataType<String> TYPE_CODE = SQLDataType.VARCHAR.length(36).nullable(true);
	
	public static final DataType<String> TYPE_STRING_XXS = SQLDataType.VARCHAR.length(1);
	public static final DataType<String> TYPE_STRING_XS = SQLDataType.VARCHAR.length(16);
	public static final DataType<String> TYPE_STRING_S = SQLDataType.VARCHAR.length(64);
	public static final DataType<String> TYPE_STRING_M = SQLDataType.VARCHAR.length(128);
	public static final DataType<String> TYPE_STRING_L = SQLDataType.VARCHAR.length(512);
	public static final DataType<String> TYPE_STRING_XL = SQLDataType.VARCHAR.length(1024);
	
	public static final DataType<Double> TYPE_DOUBLE = SQLDataType.DOUBLE;
	
	public static final DataType<byte[]> TYPE_BLOB = SQLDataType.VARBINARY;
	public static final DataType<UInteger> TYPE_SIZE = SQLDataType.INTEGERUNSIGNED;
	
	public static String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final DataType<String> TYPE_TIMESTAMP = SQLDataType.VARCHAR.length(DEFAULT_TIMESTAMP_PATTERN.length());
	public static final DataType<Date> TYPE_DATE = SQLDataType.DATE;
	
	public static final SQLDialect SQL_DIALECT = GeneratorApplication.DB_DIALECT;

	String getSchemaName();
	String createSQLInternal();
}
