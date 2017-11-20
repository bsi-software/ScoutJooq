/*
 * This file is generated by jOOQ.
*/
package com.acme.application.database.or.pg_catalog.tables;


import com.acme.application.database.or.pg_catalog.Indexes;
import com.acme.application.database.or.pg_catalog.PgCatalog;
import com.acme.application.database.or.pg_catalog.tables.records.PgProcRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Index;
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
public class PgProc extends TableImpl<PgProcRecord> {

    private static final long serialVersionUID = 928192109;

    /**
     * The reference instance of <code>pg_catalog.pg_proc</code>
     */
    public static final PgProc PG_PROC = new PgProc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PgProcRecord> getRecordType() {
        return PgProcRecord.class;
    }

    /**
     * The column <code>pg_catalog.pg_proc.proname</code>.
     */
    public final TableField<PgProcRecord, String> PRONAME = createField("proname", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.pronamespace</code>.
     */
    public final TableField<PgProcRecord, Long> PRONAMESPACE = createField("pronamespace", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proowner</code>.
     */
    public final TableField<PgProcRecord, Long> PROOWNER = createField("proowner", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.prolang</code>.
     */
    public final TableField<PgProcRecord, Long> PROLANG = createField("prolang", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.procost</code>.
     */
    public final TableField<PgProcRecord, Float> PROCOST = createField("procost", org.jooq.impl.SQLDataType.REAL.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.prorows</code>.
     */
    public final TableField<PgProcRecord, Float> PROROWS = createField("prorows", org.jooq.impl.SQLDataType.REAL.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.provariadic</code>.
     */
    public final TableField<PgProcRecord, Long> PROVARIADIC = createField("provariadic", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.protransform</code>.
     */
    public final TableField<PgProcRecord, String> PROTRANSFORM = createField("protransform", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proisagg</code>.
     */
    public final TableField<PgProcRecord, Boolean> PROISAGG = createField("proisagg", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proiswindow</code>.
     */
    public final TableField<PgProcRecord, Boolean> PROISWINDOW = createField("proiswindow", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.prosecdef</code>.
     */
    public final TableField<PgProcRecord, Boolean> PROSECDEF = createField("prosecdef", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proleakproof</code>.
     */
    public final TableField<PgProcRecord, Boolean> PROLEAKPROOF = createField("proleakproof", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proisstrict</code>.
     */
    public final TableField<PgProcRecord, Boolean> PROISSTRICT = createField("proisstrict", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proretset</code>.
     */
    public final TableField<PgProcRecord, Boolean> PRORETSET = createField("proretset", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.provolatile</code>.
     */
    public final TableField<PgProcRecord, String> PROVOLATILE = createField("provolatile", org.jooq.impl.SQLDataType.CHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proparallel</code>.
     */
    public final TableField<PgProcRecord, String> PROPARALLEL = createField("proparallel", org.jooq.impl.SQLDataType.CHAR.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.pronargs</code>.
     */
    public final TableField<PgProcRecord, Short> PRONARGS = createField("pronargs", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.pronargdefaults</code>.
     */
    public final TableField<PgProcRecord, Short> PRONARGDEFAULTS = createField("pronargdefaults", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.prorettype</code>.
     */
    public final TableField<PgProcRecord, Long> PRORETTYPE = createField("prorettype", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proargtypes</code>.
     */
    public final TableField<PgProcRecord, Long[]> PROARGTYPES = createField("proargtypes", org.jooq.impl.SQLDataType.BIGINT.getArrayDataType(), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proallargtypes</code>.
     */
    public final TableField<PgProcRecord, Long[]> PROALLARGTYPES = createField("proallargtypes", org.jooq.impl.SQLDataType.BIGINT.getArrayDataType(), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proargmodes</code>.
     */
    public final TableField<PgProcRecord, String[]> PROARGMODES = createField("proargmodes", org.jooq.impl.SQLDataType.CHAR.getArrayDataType(), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proargnames</code>.
     */
    public final TableField<PgProcRecord, String[]> PROARGNAMES = createField("proargnames", org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<PgProcRecord, Object> PROARGDEFAULTS = createField("proargdefaults", org.jooq.impl.DefaultDataType.getDefaultDataType("pg_node_tree"), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.protrftypes</code>.
     */
    public final TableField<PgProcRecord, Long[]> PROTRFTYPES = createField("protrftypes", org.jooq.impl.SQLDataType.BIGINT.getArrayDataType(), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.prosrc</code>.
     */
    public final TableField<PgProcRecord, String> PROSRC = createField("prosrc", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.probin</code>.
     */
    public final TableField<PgProcRecord, String> PROBIN = createField("probin", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proconfig</code>.
     */
    public final TableField<PgProcRecord, String[]> PROCONFIG = createField("proconfig", org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>pg_catalog.pg_proc.proacl</code>.
     */
    public final TableField<PgProcRecord, String[]> PROACL = createField("proacl", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * Create a <code>pg_catalog.pg_proc</code> table reference
     */
    public PgProc() {
        this(DSL.name("pg_proc"), null);
    }

    /**
     * Create an aliased <code>pg_catalog.pg_proc</code> table reference
     */
    public PgProc(String alias) {
        this(DSL.name(alias), PG_PROC);
    }

    /**
     * Create an aliased <code>pg_catalog.pg_proc</code> table reference
     */
    public PgProc(Name alias) {
        this(alias, PG_PROC);
    }

    private PgProc(Name alias, Table<PgProcRecord> aliased) {
        this(alias, aliased, null);
    }

    private PgProc(Name alias, Table<PgProcRecord> aliased, Field<?>[] parameters) {
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
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.PG_PROC_PRONAME_ARGS_NSP_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgProc as(String alias) {
        return new PgProc(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgProc as(Name alias) {
        return new PgProc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PgProc rename(String name) {
        return new PgProc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PgProc rename(Name name) {
        return new PgProc(name, null);
    }
}