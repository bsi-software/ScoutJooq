package com.acme.application.server.sql;

import javax.annotation.PostConstruct;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.CreateImmediately;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.exception.ExceptionHandler;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.server.ServerProperties;
import com.acme.application.server.security.PermissionService;
import com.acme.application.server.text.TextService;
import com.acme.application.shared.code.ApplicationCodeUtility;

@ApplicationScoped
@CreateImmediately
public class DatabaseSetupService {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseSetupService.class);

	@PostConstruct
	public void autoCreateDatabase() {
		
		if (CONFIG.getPropertyValue(ServerProperties.DatabaseAutoCreateProperty.class)) {
			try {
				RunContext context = BEANS.get(SuperUserRunContextProducer.class).produce();
				IRunnable runnable = new IRunnable() {

					@Override
					public void run() throws Exception {
						BEANS.all(IDataStoreService.class)
						.forEach(store -> store.create());

						initializeTexts();
						initializeCodes();						
					}
				};

				context.run(runnable);
			}
			catch (RuntimeException e) {
				BEANS.get(ExceptionHandler.class).handle(e);
			}
		}
	}

	private void initializeCodes() {
		LOG.info("Initialize codes");
		ApplicationCodeUtility.reloadAll();
	}

	private void initializeTexts() {
		LOG.info("Initialize texts");
		BEANS.get(PermissionService.class).checkTranslations();
		BEANS.get(TextService.class).invalidateCache();
	}
}
