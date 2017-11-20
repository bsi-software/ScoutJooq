/*
 * This file is generated by jOOQ.
*/
package com.acme.application.database.or.pg_catalog.tables;


import com.acme.application.database.or.pg_catalog.PgCatalog;
import com.acme.application.database.or.pg_catalog.tables.records.JsonbPopulateRecordsetRecord;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class JsonbPopulateRecordset extends TableImpl<JsonbPopulateRecordsetRecord> {

    private static final long serialVersionUID = 2055509469;

    /**
     * The reference instance of <code>pg_catalog.jsonb_populate_recordset</code>
     */
    public static final JsonbPopulateRecordset JSONB_POPULATE_RECORDSET = new JsonbPopulateRecordset();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JsonbPopulateRecordsetRecord> getRecordType() {
        return JsonbPopulateRecordsetRecord.class;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<JsonbPopulateRecordsetRecord, Object> JSONB_POPULATE_RECORDSET_ = createField("jsonb_populate_recordset", org.jooq.impl.DefaultDataType.getDefaultDataType("anyelement"), this, "");

    /**
     * Create a <code>pg_catalog.jsonb_populate_recordset</code> table reference
     */
    public JsonbPopulateRecordset() {
        this(DSL.name("jsonb_populate_recordset"), null);
    }

    /**
     * Create an aliased <code>pg_catalog.jsonb_populate_recordset</code> table reference
     */
    public JsonbPopulateRecordset(String alias) {
        this(DSL.name(alias), JSONB_POPULATE_RECORDSET);
    }

    /**
     * Create an aliased <code>pg_catalog.jsonb_populate_recordset</code> table reference
     */
    public JsonbPopulateRecordset(Name alias) {
        this(alias, JSONB_POPULATE_RECORDSET);
    }

    private JsonbPopulateRecordset(Name alias, Table<JsonbPopulateRecordsetRecord> aliased) {
        this(alias, aliased, new Field[2]);
    }

    private JsonbPopulateRecordset(Name alias, Table<JsonbPopulateRecordsetRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return PgCatalog.PG_CATALOG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonbPopulateRecordset as(String alias) {
        return new JsonbPopulateRecordset(DSL.name(alias), this, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonbPopulateRecordset as(Name alias) {
        return new JsonbPopulateRecordset(alias, this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public JsonbPopulateRecordset rename(String name) {
        return new JsonbPopulateRecordset(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public JsonbPopulateRecordset rename(Name name) {
        return new JsonbPopulateRecordset(name, null, parameters);
    }

    /**
     * Call this table-valued function
     */
    public JsonbPopulateRecordset call(Object __1, Object __2) {
        return new JsonbPopulateRecordset(DSL.name(getName()), null, new Field[] { 
              DSL.val(__1, org.jooq.impl.DefaultDataType.getDefaultDataType("anyelement"))
            , DSL.val(__2, org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"))
        });
    }

    /**
     * Call this table-valued function
     */
    public JsonbPopulateRecordset call(Field<Object> __1, Field<Object> __2) {
        return new JsonbPopulateRecordset(DSL.name(getName()), null, new Field[] { 
              __1
            , __2
        });
    }
}