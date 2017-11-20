package com.acme.application.ui.html;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.DefaultRuntimeExceptionTranslator;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.server.commons.authentication.ServletFilterHelper;
import org.eclipse.scout.rt.shared.SharedConfigProperties.BackendUrlProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Order(1000)
public class CredentialVerifierProxy implements ICredentialVerifier {

	protected URL m_remoteAuthUrl;
	//	protected ICredentialVerifier m_externalPasswordVerifier;

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CredentialVerifierProxy.class);

	@PostConstruct
	protected void postConstruct() {
		m_remoteAuthUrl = getRemoteAuthUrl();
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	@Override
	public int verify(final String username, final char[] passwordPlainText) throws IOException {
		final HttpURLConnection con = (HttpURLConnection) m_remoteAuthUrl.openConnection();
		con.setDefaultUseCaches(false);
		con.setUseCaches(false);
		con.setRequestProperty(ServletFilterHelper.HTTP_HEADER_AUTHORIZATION, BEANS.get(ServletFilterHelper.class)
				.createBasicAuthRequest(username, passwordPlainText));

		switch (con.getResponseCode()) {
		case HttpServletResponse.SC_OK:
			return AUTH_OK;
		case HttpServletResponse.SC_FORBIDDEN:
		default:
			return AUTH_FORBIDDEN;
		}
	}

	/**
	 * Creates the remote authentication url as '{@link BackendUrlProperty}/auth'.
	 * @return
	 */
	protected URL getRemoteAuthUrl() {
		try {
			String baseUrl = CONFIG.getPropertyValue(BackendUrlProperty.class);
			return new URL(baseUrl + "/auth");
		}
		catch (final MalformedURLException e) {
			throw BEANS.get(DefaultRuntimeExceptionTranslator.class).translate(e);
		}
	}
}
