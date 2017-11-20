#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.security;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.platform.util.Assertions;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.commons.authentication.ServletFilterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Bean
public class UserAuthenticator {
  private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticator.class);

  protected UserAuthConfig m_config;

  public UserAuthenticator init() {
    init(new UserAuthConfig());
    return this;
  }

  public void init(final UserAuthConfig config) {
    m_config = config;
    Assertions.assertNotNull(m_config.getCredentialVerifier(), "CredentialVerifier must not be null");
  }

  public void destroy() {
    // NOOP
  }

  public boolean handle(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
    if (!m_config.isEnabled()) {
      return false;
    }

    if (!isAuthRequest(req)) {
      LOG.debug("Authenicator: not auth request.");
      return false;
    }

    handleAuthRequest(req, resp);

    return true;
  }

  /**
   * Method invoked to handle an authentication request targeted to '/auth' URL.
   * <p>
   * If the user's password requires external verification, this authentication returns with 403 response code and with
   * the HTTP header {@link IUserStoreCredentialVerifier${symbol_pound}HTTP_HEADER_X_AUTH_STATUS} set to
   * {@link IUserStoreCredentialVerifier${symbol_pound}AUTH_EXTERNAL_PASSWORD_VERIFICATION_REQUIRED}.
   * <p>
   * However, because this class is of the characteristics of an Authenticator and not an AccessController, the
   * request's HTTP session is not touched or changed, nor the filter-chain continued.
   */
  protected void handleAuthRequest(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
    final Entry<String, char[]> credentials = readCredentials(req);
    if (credentials == null) {
      handleForbidden(ICredentialVerifier.AUTH_CREDENTIALS_REQUIRED, resp);
      return;
    }

    // Never cache authentication requests.
    resp.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
    resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
    resp.setDateHeader("Expires", 0); // prevents caching at the proxy server

    final int status = m_config.getCredentialVerifier().verify(credentials.getKey(), credentials.getValue());
    if (status != ICredentialVerifier.AUTH_OK) {
      handleForbidden(status, resp);
    }
  }

  /**
   * Method invoked if the user could not be verified.
   */
  protected void handleForbidden(final int status, final HttpServletResponse resp) throws IOException, ServletException {
    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  /**
   * Reads the credentials from the request.
   * <p>
   * The default implementation expects the credentials to be included in HTTP headers in the BASIC Authentication
   * format.
   */
  protected Entry<String, char[]> readCredentials(final HttpServletRequest req) {
    final String[] basicCredentials = BEANS.get(ServletFilterHelper.class).parseBasicAuthRequest(req);
    if (basicCredentials == null || basicCredentials.length != 2) {
      return null;
    }

    final String user = basicCredentials[0];
    if (StringUtility.isNullOrEmpty(user)) {
      return null;
    }

    final String password = basicCredentials[1];
    if (StringUtility.isNullOrEmpty(password)) {
      return null;
    }

    return new SimpleEntry<>(user, password.toCharArray());
  }

  protected boolean isAuthRequest(final HttpServletRequest request) {
    final String url = request.getRequestURI();
    return url != null && url.endsWith("/auth");
  }

  /**
   * Configuration for {@link UserAuthenticator}.
   */
  public static class UserAuthConfig {

    private boolean m_enabled = true;
    private ICredentialVerifier m_credentialVerifier = BEANS.get(DatabaseCredentialVerifier.class);

    public boolean isEnabled() {
      return m_enabled;
    }

    public UserAuthConfig withEnabled(final boolean enabled) {
      m_enabled = enabled;
      return this;
    }

    public ICredentialVerifier getCredentialVerifier() {
      return m_credentialVerifier;
    }

    public UserAuthConfig withCredentialVerifier(final ICredentialVerifier credentialVerifier) {
      m_credentialVerifier = credentialVerifier;
      return this;
    }
  }
}
