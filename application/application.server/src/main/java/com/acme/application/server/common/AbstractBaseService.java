package com.acme.application.server.common;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import com.acme.application.server.sql.AbstractJooqService;

public abstract class AbstractBaseService<TABLE extends Table<RECORD>, RECORD extends Record>
        extends AbstractJooqService implements IBaseService<TABLE, RECORD> {

    @Override
    public boolean exists(String id) {
        try {
            return exists(getContext(), id);
        } catch (ProcessingException e) {
            getLogger().error("Failed to execute exists(). id: {}, table: {}. exception: ", id, getTable(), e);
        }

        return false;
    }

    @Override
    public RECORD get(String id) {
        return getContext()
                .selectFrom(getTable())
                .where(getIdColumn().eq(id))
                .fetchOne();
    }

    @Override
    public List<RECORD> getAll() {
        return getContext()
                .selectFrom(getTable())
                .fetchStream()
                .collect(Collectors.toList());
    }

    @Override
    public void store(String id, RECORD record) {
        DSLContext context = getContext();
        if (exists(context, id)) {
            context
                    .update(getTable())
                    .set(record)
                    .where(getIdColumn().eq(id))
                    .execute();
        } else {
            context
                    .insertInto(getTable())
                    .set(record)
                    .execute();
        }
    }

    @Override
    public int delete(String id) {
    	return getContext()
    		.delete(getTable())
    		.where(getIdColumn().eq(id))
    		.execute();

    }

    /**
     * Returns true iff a record with the provided id exists using the specified
     * context
     */
    private boolean exists(DSLContext context, String id) {
        return context.fetchExists(
                context.select()
                        .from(getTable())
                        .where(getIdColumn().eq(id)));
    }
}
