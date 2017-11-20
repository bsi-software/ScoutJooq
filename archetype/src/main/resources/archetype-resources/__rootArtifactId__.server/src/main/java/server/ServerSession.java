#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server;

import java.util.Locale;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.server.AbstractServerSession;
import org.eclipse.scout.rt.server.session.ServerSessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.server.text.TextService;
import ${package}.server.user.UserService;
import ${package}.shared.code.ApplicationCodeUtility;

/**
 * <h3>{@link ServerSession}</h3>
 */
public class ServerSession extends AbstractServerSession {
	
	public static final String SUPER_USER_ID = "system";

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	private Locale userLocale = null;

	public ServerSession() {
		super(true);
	}

	public Locale getLocale() {
		return userLocale;
	}

	/**
	 * @return The {@link ServerSession} which is associated with the current
	 *         thread, or {@code null} if not found.
	 */
	public static ServerSession get() {
		return ServerSessionProvider.currentSession(ServerSession.class);
	}

	@Override
	protected void execLoadSession() {
		String userId = getUserId();

		if(!SUPER_USER_ID.equals(userId)) {
			userLocale = BEANS.get(UserService.class).getLocale(userId);			
			ApplicationCodeUtility.reloadAll();
			BEANS.get(TextService.class).invalidateCache();
			
			LOG.info("Created a new server session for '{}' with locale '{}'", userId, userLocale);			
		}
	}
}
