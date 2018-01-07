#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.preferences;

import java.util.Properties;

import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IUserPreferenceService {

    Properties load(String userId, String node);

    void store(String userId, String node, Properties prefs);

}
