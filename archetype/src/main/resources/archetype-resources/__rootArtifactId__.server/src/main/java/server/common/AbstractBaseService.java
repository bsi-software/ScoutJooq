#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

import ${package}.server.sql.JooqService;

public abstract class AbstractBaseService<TABLE extends Table<RECORD>, RECORD extends Record> extends JooqService implements IBaseService<TABLE, RECORD> {

	@Override
	public boolean exists(String id) {
		try(Connection connection = getConnection()) {
			return exists(getContext(connection), id);
		} 
		catch (SQLException e) {
			getLogger().error("Failed to execute exists(). id: {}, table: {}. exception: ", id, getTable(), e);
		}

		return false;
	}

	@Override
	public RECORD get(String id) {
		try(Connection connection = getConnection()) {
			return getContext(connection)
					.selectFrom(getTable())
					.where(getIdColumn().eq(id))
					.fetchOne();
		} 
		catch (SQLException e) {
			getLogger().error("Failed to execute get(). id: {}, table: {}. exception: ", id, getTable(), e);
		}

		return null;
	}

	@Override
	public List<RECORD> getAll() {
		try(Connection connection = getConnection()) {
			return getContext(connection)
					.selectFrom(getTable())
					.fetchStream()
					.collect(Collectors.toList());
		} 
		catch (SQLException e) {
			getLogger().error("Failed to execute getAll(). table: {}. exception: ", getTable(), e);
		}

		return new ArrayList<RECORD>();
	}

	@Override
	public void store(String id, RECORD record) {
		try(Connection connection = getConnection()) {
			DSLContext context = getContext(connection);
			if (exists(context, id)) {
				context
				.update(getTable())
				.set(record)
				.where(getIdColumn().eq(id))
				.execute();
			} 
			else {
				context
				.insertInto(getTable())
				.set(record)
				.execute();
			}
		}
		catch (SQLException e) {
			getLogger().error("Failed to execute store(). id: {}, record: {}. exception: ", id, record, e);
		}
	}

	/**
	 * Returns true iff a record with the provided id exists using the specified context
	 */
	private boolean exists(DSLContext context, String id) {
		return context.fetchExists(
				context.select()
				.from(getTable())
				.where(getIdColumn().eq(id)));
	}
}
