/*
 * This file is generated by jOOQ.
*/
package ${package}.database.or.pg_catalog.tables;


import ${package}.database.generator.converter.LongConverter;
import ${package}.database.or.pg_catalog.PgCatalog;
import ${package}.database.or.pg_catalog.tables.records.TxidSnapshotXipRecord;

import java.math.BigDecimal;

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
public class TxidSnapshotXip extends TableImpl<TxidSnapshotXipRecord> {

    private static final long serialVersionUID = -450079292;

    /**
     * The reference instance of <code>pg_catalog.txid_snapshot_xip</code>
     */
    public static final TxidSnapshotXip TXID_SNAPSHOT_XIP = new TxidSnapshotXip();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TxidSnapshotXipRecord> getRecordType() {
        return TxidSnapshotXipRecord.class;
    }

    /**
     * The column <code>pg_catalog.txid_snapshot_xip.txid_snapshot_xip</code>.
     */
    public final TableField<TxidSnapshotXipRecord, BigDecimal> TXID_SNAPSHOT_XIP_ = createField("txid_snapshot_xip", org.jooq.impl.SQLDataType.BIGINT, this, "", new LongConverter());

    /**
     * Create a <code>pg_catalog.txid_snapshot_xip</code> table reference
     */
    public TxidSnapshotXip() {
        this(DSL.name("txid_snapshot_xip"), null);
    }

    /**
     * Create an aliased <code>pg_catalog.txid_snapshot_xip</code> table reference
     */
    public TxidSnapshotXip(String alias) {
        this(DSL.name(alias), TXID_SNAPSHOT_XIP);
    }

    /**
     * Create an aliased <code>pg_catalog.txid_snapshot_xip</code> table reference
     */
    public TxidSnapshotXip(Name alias) {
        this(alias, TXID_SNAPSHOT_XIP);
    }

    private TxidSnapshotXip(Name alias, Table<TxidSnapshotXipRecord> aliased) {
        this(alias, aliased, new Field[1]);
    }

    private TxidSnapshotXip(Name alias, Table<TxidSnapshotXipRecord> aliased, Field<?>[] parameters) {
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
    public TxidSnapshotXip as(String alias) {
        return new TxidSnapshotXip(DSL.name(alias), this, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TxidSnapshotXip as(Name alias) {
        return new TxidSnapshotXip(alias, this, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public TxidSnapshotXip rename(String name) {
        return new TxidSnapshotXip(DSL.name(name), null, parameters);
    }

    /**
     * Rename this table
     */
    @Override
    public TxidSnapshotXip rename(Name name) {
        return new TxidSnapshotXip(name, null, parameters);
    }

    /**
     * Call this table-valued function
     */
    public TxidSnapshotXip call(Object __1) {
        return new TxidSnapshotXip(DSL.name(getName()), null, new Field[] { 
              DSL.val(__1, org.jooq.impl.DefaultDataType.getDefaultDataType("txid_snapshot"))
        });
    }

    /**
     * Call this table-valued function
     */
    public TxidSnapshotXip call(Field<Object> __1) {
        return new TxidSnapshotXip(DSL.name(getName()), null, new Field[] { 
              __1
        });
    }
}