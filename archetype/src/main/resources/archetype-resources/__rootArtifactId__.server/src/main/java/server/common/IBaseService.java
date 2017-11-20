#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.common;

import java.util.List;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.slf4j.Logger;

@ApplicationScoped
public interface IBaseService<TABLE extends Table<RECORD>, RECORD extends Record> {

	/**
	 * Returns the table object associated with this service.
	 */
	TABLE getTable();
	
	/**
	 * Returns the id column for the table object associated with this service.
	 */
	Field<String> getIdColumn();
	
	/**
	 * Returns true iff a record with the specified id exists.
	 */
	boolean exists(String id);
	
	/**
	 * Returns the record for the specified id. 
	 * Returns null if no such record exists.
	 */
	RECORD get(String id);
	
	/**
	 * Returns all available records.
	 */
	List<RECORD> getAll();
	
	/**
	 * Persists the provided record based on the id specified.
	 * If no record with this id exists a new record is created, otherwise the existing record is updated.
	 */
	void store(String id, RECORD record);
	
	/**
	 * Returns a logger instance for this class.
	 * @return
	 */
	Logger getLogger();
}
