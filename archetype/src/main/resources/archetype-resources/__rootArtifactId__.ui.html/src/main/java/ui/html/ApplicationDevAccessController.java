#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.ui.html;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.Platform;
import org.eclipse.scout.rt.platform.security.IPrincipalProducer;
import org.eclipse.scout.rt.server.commons.authentication.AnonymousAccessController;
import org.eclipse.scout.rt.server.commons.authentication.AnonymousAccessController.AnonymousAuthConfig;
import org.eclipse.scout.rt.server.commons.authentication.IAccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class copied from {@link org.eclipse.scout.rt.server.commons.authentication.DevelopmentAccessController}
 */
@Bean
public class ApplicationDevAccessController implements IAccessController {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationDevAccessController.class);

  private final AnonymousAccessController m_anonymousAccessController = BEANS.get(AnonymousAccessController.class);
  private final AnonymousAuthConfig m_config = new AnonymousAuthConfig();

  private final AtomicBoolean m_warningLogged = new AtomicBoolean(false);

  public ApplicationDevAccessController init() {
    init(new DevelopmentAuthConfig());
    return this;
  }

  /**
   * Works with username 'root' instead of System.getProperty("user.name").
   * Rest as implemented in {@link org.eclipse.scout.rt.server.commons.authentication.DevelopmentAccessController}.
   */
  public ApplicationDevAccessController init(final DevelopmentAuthConfig config) {
    AnonymousAuthConfig anonymousAuthConfig = m_config
        .withEnabled(config.isEnabled() && Platform.get().inDevelopmentMode())
        .withUsername("root")
        .withPutPrincipalOnSession(config.isPutPrincipalOnSession());
    if (config.getPrincipalProducer() != null) {
      anonymousAuthConfig.withPrincipalProducer(config.getPrincipalProducer());
    }
    m_anonymousAccessController.init(anonymousAuthConfig);
    return this;
  }

  @Override
  public boolean handle(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
    if (m_config.isEnabled() && m_warningLogged.compareAndSet(false, true)) {
      LOG.warn("+++ Development access control with user {}", m_config.getUsername());
    }
    return m_anonymousAccessController.handle(request, response, chain);
  }

  @Override
  public void destroy() {
    m_anonymousAccessController.destroy();
  }

  public static class DevelopmentAuthConfig {

    private boolean m_enabled = true;
    private IPrincipalProducer m_principalProducer = null;
    private boolean m_putPrincipalOnSession = true;

    public boolean isEnabled() {
      return m_enabled;
    }

    public DevelopmentAuthConfig withEnabled(final boolean enabled) {
      m_enabled = enabled;
      return this;
    }

    public IPrincipalProducer getPrincipalProducer() {
      return m_principalProducer;
    }

    /**
     * Sets the {@link IPrincipalProducer} to produce a {@link Principal} for authenticated users. By default, the inner
     * {@link AnonymousAccessController}'s principal producer is used.
     */
    public DevelopmentAuthConfig withPrincipalProducer(final IPrincipalProducer principalProducer) {
      m_principalProducer = principalProducer;
      return this;
    }

    public boolean isPutPrincipalOnSession() {
      return m_putPrincipalOnSession;
    }

    public DevelopmentAuthConfig withPutPrincipalOnSession(final boolean putPrincipalOnSession) {
      m_putPrincipalOnSession = putPrincipalOnSession;
      return this;
    }
  }
}
