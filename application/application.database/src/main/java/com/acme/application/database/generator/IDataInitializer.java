package com.acme.application.database.generator;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.jooq.DSLContext;

@ApplicationScoped
public interface IDataInitializer {

	void initialize(DSLContext context);
	void addSamples(DSLContext context);
}
