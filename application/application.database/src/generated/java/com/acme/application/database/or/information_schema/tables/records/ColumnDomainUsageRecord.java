/*
 * This file is generated by jOOQ.
*/
package com.acme.application.database.or.information_schema.tables.records;


import com.acme.application.database.or.information_schema.tables.ColumnDomainUsage;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;


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
public class ColumnDomainUsageRecord extends TableRecordImpl<ColumnDomainUsageRecord> implements Record7<String, String, String, String, String, String, String> {

    private static final long serialVersionUID = -976135244;

    /**
     * Setter for <code>information_schema.column_domain_usage.domain_catalog</code>.
     */
    public void setDomainCatalog(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.domain_catalog</code>.
     */
    public String getDomainCatalog() {
        return (String) get(0);
    }

    /**
     * Setter for <code>information_schema.column_domain_usage.domain_schema</code>.
     */
    public void setDomainSchema(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.domain_schema</code>.
     */
    public String getDomainSchema() {
        return (String) get(1);
    }

    /**
     * Setter for <code>information_schema.column_domain_usage.domain_name</code>.
     */
    public void setDomainName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.domain_name</code>.
     */
    public String getDomainName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>information_schema.column_domain_usage.table_catalog</code>.
     */
    public void setTableCatalog(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.table_catalog</code>.
     */
    public String getTableCatalog() {
        return (String) get(3);
    }

    /**
     * Setter for <code>information_schema.column_domain_usage.table_schema</code>.
     */
    public void setTableSchema(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.table_schema</code>.
     */
    public String getTableSchema() {
        return (String) get(4);
    }

    /**
     * Setter for <code>information_schema.column_domain_usage.table_name</code>.
     */
    public void setTableName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.table_name</code>.
     */
    public String getTableName() {
        return (String) get(5);
    }

    /**
     * Setter for <code>information_schema.column_domain_usage.column_name</code>.
     */
    public void setColumnName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>information_schema.column_domain_usage.column_name</code>.
     */
    public String getColumnName() {
        return (String) get(6);
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, String, String, String, String, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, String, String, String, String, String, String> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.DOMAIN_CATALOG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.DOMAIN_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.DOMAIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.TABLE_CATALOG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.TABLE_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.TABLE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return ColumnDomainUsage.COLUMN_DOMAIN_USAGE.COLUMN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getDomainCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getDomainSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getDomainName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getTableCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getTableSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getDomainCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getDomainSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getDomainName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getTableCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getTableSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value1(String value) {
        setDomainCatalog(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value2(String value) {
        setDomainSchema(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value3(String value) {
        setDomainName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value4(String value) {
        setTableCatalog(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value5(String value) {
        setTableSchema(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value6(String value) {
        setTableName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord value7(String value) {
        setColumnName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnDomainUsageRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ColumnDomainUsageRecord
     */
    public ColumnDomainUsageRecord() {
        super(ColumnDomainUsage.COLUMN_DOMAIN_USAGE);
    }

    /**
     * Create a detached, initialised ColumnDomainUsageRecord
     */
    public ColumnDomainUsageRecord(String domainCatalog, String domainSchema, String domainName, String tableCatalog, String tableSchema, String tableName, String columnName) {
        super(ColumnDomainUsage.COLUMN_DOMAIN_USAGE);

        set(0, domainCatalog);
        set(1, domainSchema);
        set(2, domainName);
        set(3, tableCatalog);
        set(4, tableSchema);
        set(5, tableName);
        set(6, columnName);
    }
}