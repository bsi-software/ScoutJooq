#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.preferences;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.services.common.prefs.AbstractUserPreferencesStorageService;
import org.eclipse.scout.rt.shared.services.common.prefs.IPreferences;

import ${package}.shared.preferences.IUserPreferenceService;

/**
 * 
 * @author Florian
 *
 */
@ApplicationScoped
@Order(500)
public class UserPreferenceStorageService extends AbstractUserPreferencesStorageService {

    private IUserPreferenceService m_service = BEANS.get(IUserPreferenceService.class);

    @Override
    public void flush(IPreferences prefs) {
        Properties props = new Properties();
        convertToProperties(prefs, props);
        m_service.store(getUserScope(prefs.userScope()), prefs.name(), props);
    }

    @Override
    protected void load(String userScope, String nodeId, IPreferences prefsToFill) {
        Properties load = m_service.load(userScope, nodeId);
        convertToPreferences(load, prefsToFill);
        
    }

    protected String getUserScope(ISession session) {
        String userId = session.getUserId();
        if (StringUtility.hasText(userId)) {
            return userId.trim();
        }
        return "anonymous";
    }

    protected void convertToProperties(IPreferences src, Properties target) {
        Set<String> keys = src.keys();
        for (String key : keys) {
          String value = src.get(key, null);
          if (value != null) {
            target.setProperty(key, value);
          }
        }
      }

      protected void convertToPreferences(Properties src, IPreferences target) {
        for (Entry<Object, Object> entry : src.entrySet()) {
          String key = (String) entry.getKey();
          String value = (String) entry.getValue();
          if (value != null) {
            target.put(key, value);
          }
        }
      }
}
