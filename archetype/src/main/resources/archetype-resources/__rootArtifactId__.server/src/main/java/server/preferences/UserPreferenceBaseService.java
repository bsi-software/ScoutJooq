#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.preferences;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.serialization.SerializationUtility;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import ${package}.database.or.core.tables.UserPreference;
import ${package}.database.or.core.tables.records.UserPreferenceRecord;
import ${package}.server.sql.AbstractJooqService;
import ${package}.shared.preferences.IUserPreferenceService;

@ApplicationScoped
public class UserPreferenceBaseService extends AbstractJooqService implements IUserPreferenceService {

    
    @Override
    public Properties load(String userId, String node) {
        UserPreferenceRecord record = getContext().selectFrom(getTable()).where(createWhereCondition(userId, node)).fetchOne();
        if (record == null) {
            return new Properties();
        }
        byte[] data = record.getData();
        try {
            return SerializationUtility.createObjectSerializer().deserialize(data, Properties.class);
        } catch (ClassNotFoundException | IOException e) {
            throw new ProcessingException("Error deserializing IPreferences", e);
        }
    }
    
    @Override
    public void store(String userId, String node, Properties prefs) {
        try {
            byte[] data = SerializationUtility.createObjectSerializer().serialize(prefs);
            UserPreferenceRecord record = new UserPreferenceRecord(userId, node, data);
            if (exists(userId, node)) {
                getContext().executeUpdate(record, createWhereCondition(userId, node));
            } else {
               getContext().executeInsert(record); 
            }
        } catch (IOException e) {
            throw new ProcessingException("Error serializing IPreferences", e);
        }
    }

    private TableField<UserPreferenceRecord, String> nodeColumn() {
        return getTable().NODE;
    }

    private TableField<UserPreferenceRecord, String> userColumn() {
        return getTable().USER;
    }
    
    private UserPreference getTable() {
        return UserPreference.USER_PREFERENCE;
    }

    private boolean exists(String userId, String node) {
        return getContext().fetchExists(DSL.selectFrom(getTable()).where(createWhereCondition(userId, node)));
    }
    
    private Condition createWhereCondition(String userId, String node) {
        return userColumn().eq(userId).and(nodeColumn().eq(node));
    }
    
}
