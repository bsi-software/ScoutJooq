/*
 * This file is generated by jOOQ.
*/
package ${package}.database.or.pg_catalog.tables;


import ${package}.database.or.pg_catalog.PgCatalog;
import ${package}.database.or.pg_catalog.tables.records.PgLogicalSlotGetChangesRecord;

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
public class PgLogicalSlotGetChanges extends TableImpl<PgLogicalSlotGetChangesRecord> {

    private static final long serialVersionUID = 1863824421;

    /**
     * The reference instance of <code>pg_catalog.pg_logical_slot_get_changes</code>
     */
    public static final PgLogicalSlotGetChanges PG_LOGICAL_SLOT_GET_CHANGES = new PgLogicalSlotGetChanges();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PgLogicalSlotGetChangesRecord> getRecordType() {
        return PgLogicalSlotGetChangesRecord.class;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<PgLogicalSlotGetChangesRecord, Object> LSN = createField("lsn", org.jooq.impl.DefaultDataType.getDefaultDataType("pg_lsn"), this, "");

    /**
     * The column <code>pg_catalog.pg_logical_slot_get_changes.xid</code>.
     */
    public final TableField<PgLogicalSlotGetChangesRecord, Long> XID = createField("xid", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>pg_catalog.pg_logical_slot_get_changes.data</code>.
     */
    public final TableField<PgLogicalSlotGetChangesRecord, String> DATA = createField("data", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>pg_catalog.pg_logical_slot_get_changes</code> table reference
     */
    public PgLogicalSlotGetChanges() {
        this(DSL.name("pg_logical_slot_get_changes"), null);
    }

    /**
     * Create an aliased <code>pg_catalog.pg_logical_slot_get_changes</code> table reference
     */
    public PgLogicalSlotGetChanges(String alias) {
        this(DSL.name(alias), PG_LOGICAL_SLOT_GET_CHANGES);
    }

    /**
     * Create an aliased <code>pg_catalog.pg_logical_slot_get_changes</code> table reference
     */
    public PgLogicalSlotGetChanges(Name alias) {
        this(alias, PG_LOGICAL_SLOT_GET_CHANGES);
    }

    private PgLogicalSlotGetChanges(Name alias, Table<PgLogicalSlotGetChangesRecord> aliased) {
        this(alias, aliased, new Field[4]);
    }

    private PgLogicalSlotGetChanges(Name alias, Table<PgLogicalSlotGetChangesRecord> aliased, Field<?>[] parameters) {
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
    public PgLogicalSlotGetChanges as(String alias) {
        return new PgLogicalSlotGetChanges(DSL.name(alias), this, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgLogicalSlotGetChanges as(Name alias) {
        return new PgLogicalSlotGetChanges(alias, this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public PgLogicalSlotGetChanges rename(String name) {
        return new PgLogicalSlotGetChanges(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public PgLogicalSlotGetChanges rename(Name name) {
        return new PgLogicalSlotGetChanges(name, null, parameters);
    }

    /**
     * Call this table-valued function
     */
    public PgLogicalSlotGetChanges call(String slotName, Object uptoLsn, Integer uptoNchanges, String[] options) {
        return new PgLogicalSlotGetChanges(DSL.name(getName()), null, new Field[] { 
              DSL.val(slotName, org.jooq.impl.SQLDataType.VARCHAR)
            , DSL.val(uptoLsn, org.jooq.impl.DefaultDataType.getDefaultDataType("pg_lsn"))
            , DSL.val(uptoNchanges, org.jooq.impl.SQLDataType.INTEGER)
            , DSL.val(options, org.jooq.impl.SQLDataType.CLOB.getArrayDataType())
        });
    }

    /**
     * Call this table-valued function
     */
    public PgLogicalSlotGetChanges call(Field<String> slotName, Field<Object> uptoLsn, Field<Integer> uptoNchanges, Field<String[]> options) {
        return new PgLogicalSlotGetChanges(DSL.name(getName()), null, new Field[] { 
              slotName
            , uptoLsn
            , uptoNchanges
            , options
        });
    }
}