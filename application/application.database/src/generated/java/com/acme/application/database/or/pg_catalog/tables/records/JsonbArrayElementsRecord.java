/*
 * This file is generated by jOOQ.
*/
package com.acme.application.database.or.pg_catalog.tables.records;


import com.acme.application.database.or.pg_catalog.tables.JsonbArrayElements;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Row1;
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
public class JsonbArrayElementsRecord extends TableRecordImpl<JsonbArrayElementsRecord> implements Record1<Object> {

    private static final long serialVersionUID = -557121746;

    /**
     * Setter for <code>pg_catalog.jsonb_array_elements.value</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public void setValue(Object value) {
        set(0, value);
    }

    /**
     * Getter for <code>pg_catalog.jsonb_array_elements.value</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public Object getValue() {
        return (Object) get(0);
    }

    // -------------------------------------------------------------------------
    // Record1 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row1<Object> fieldsRow() {
        return (Row1) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row1<Object> valuesRow() {
        return (Row1) super.valuesRow();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Field<Object> field1() {
        return JsonbArrayElements.JSONB_ARRAY_ELEMENTS.VALUE;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object component1() {
        return getValue();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object value1() {
        return getValue();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public JsonbArrayElementsRecord value1(Object value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonbArrayElementsRecord values(Object value1) {
        value1(value1);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached JsonbArrayElementsRecord
     */
    public JsonbArrayElementsRecord() {
        super(JsonbArrayElements.JSONB_ARRAY_ELEMENTS);
    }

    /**
     * Create a detached, initialised JsonbArrayElementsRecord
     */
    public JsonbArrayElementsRecord(Object value) {
        super(JsonbArrayElements.JSONB_ARRAY_ELEMENTS);

        set(0, value);
    }
}