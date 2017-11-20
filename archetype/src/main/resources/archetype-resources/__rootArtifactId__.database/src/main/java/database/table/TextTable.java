#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import java.util.Locale;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextTable extends AbstractCoreTable {

	public static final String TABLE = "TEXT";
	public static final String KEY = "KEY";
	public static final String LOCALE = "LOCALE";
	public static final String TEXT = "TEXT";

	public static final String LOCALE_DEFAULT = Locale.ROOT.toLanguageTag();
	public static final String LOCALE_GERMAN = Locale.GERMAN.toLanguageTag();


	@Override
	public String getName() {
		return TABLE;
	}

	@Override
	public String createSQLInternal() {
		return getContext()
				.createTable(getName())
				.column(KEY, TYPE_STRING_M.nullable(false))
				.column(LOCALE, TYPE_STRING_S.nullable(false))
				.column(TEXT, TYPE_STRING_L.nullable(false))
				.constraints(
						DSL.constraint(getPKName()).primaryKey(KEY, LOCALE))
				.getSQL();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(TextTable.class);
	}
}