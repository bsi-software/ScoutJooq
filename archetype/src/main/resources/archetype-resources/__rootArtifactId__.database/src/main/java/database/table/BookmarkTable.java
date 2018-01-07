#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookmarkTable extends AbstractCoreTable {
    
    public static final Logger LOG = LoggerFactory.getLogger(BookmarkTable.class);
    private static final String TABLE = "bookmark";
    private static final String USER = "user";
    private static final String DATA = "data";

    @Override
    public String createSQLInternal() {
        return getContext().createTable(getName())
            .column(USER, TYPE_STRING_L)
            .column(DATA, TYPE_BLOB)
            .constraint(DSL.constraint(getPKName()).primaryKey(USER))
            .getSQL();
    }

    @Override
    public String getName() {
        return TABLE;
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}
