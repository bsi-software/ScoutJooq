#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client;

import java.util.Locale;

import org.eclipse.scout.rt.client.AbstractClientSession;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.shared.code.ApplicationCodeUtility;
import ${package}.shared.text.ITextService;
import ${package}.shared.user.IUserService;

/**
 * <h3>{@link ClientSession}</h3>
 */
public class ClientSession extends AbstractClientSession {
	private static final Logger LOG = LoggerFactory.getLogger(ClientSession.class);

	public ClientSession() {
		super(true);
	}

	/**
	 * @return The {@link IClientSession} which is associated with the current
	 *         thread, or {@code null} if not found.
	 */
	public static ClientSession get() {
		return ClientSessionProvider.currentSession(ClientSession.class);
	}

	@Override
	protected void execLoadSession() {
	    initializeSharedVariables();
	    initializePermissions();
		initializeLocaleTextsAndCodes();
		
		setDesktop(new Desktop());
		
		LOG.info("Created new client session for '{}' with locale '{}'", getUserId(), getLocale());
	}

	private void initializePermissions() {
		BEANS.get(IAccessControlService.class).clearCacheOfCurrentUser();
	}

	public void initializeLocaleTextsAndCodes() {
		Locale userLocale = BEANS.get(IUserService.class).getLocale(getUserId());
		setLocale(userLocale);
		BEANS.get(ITextService.class).invalidateCache();
		ApplicationCodeUtility.reloadAll();
	}
}
