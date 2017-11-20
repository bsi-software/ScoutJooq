package com.acme.application.server.security;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.nls.NlsLocale;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.platform.transaction.TransactionScope;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.context.ServerRunContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.server.user.UserService;

@Order(1000)
public class DatabaseCredentialVerifier implements ICredentialVerifier {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseCredentialVerifier.class);

	@Override
	public int verify(final String username, final char[] passwordPlainText) throws IOException {

		return ServerRunContexts.copyCurrent(true).withTransactionScope(TransactionScope.REQUIRES_NEW).call(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				if (StringUtility.isNullOrEmpty(username)) {
					LOG.warn("Provided username is null or empty, no login possible");
					return AUTH_CREDENTIALS_REQUIRED;
				}

				String user = username.toLowerCase(NlsLocale.get());

				if (!BEANS.get(UserService.class).userIsActive(user)) {
					LOG.warn("Provided user is deactivated, no login possible");
					return AUTH_FORBIDDEN;
				}
				
				String passwordPlain = new String(passwordPlainText);

				if (!BEANS.get(UserService.class).verifyPassword(user, passwordPlain)) {
					LOG.warn("Provided user does not exist or password does not match, no login possible");
					return AUTH_FORBIDDEN;
				}

				return AUTH_OK;
			}
		});
	}
}
