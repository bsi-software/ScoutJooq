/*
 * This file is generated by jOOQ.
*/
package ${package}.database.or.pg_catalog.routines;


import ${package}.database.or.pg_catalog.PgCatalog;

import javax.annotation.Generated;

import org.jooq.impl.AbstractRoutine;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PgAdvisoryUnlockAll extends AbstractRoutine<java.lang.Void> {

    private static final long serialVersionUID = 196591963;

    /**
     * Create a new routine call instance
     */
    public PgAdvisoryUnlockAll() {
        super("pg_advisory_unlock_all", PgCatalog.PG_CATALOG);
    }
}