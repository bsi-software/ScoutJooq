package com.acme.application.server.testing;

import org.eclipse.scout.rt.platform.Replace;

import com.acme.application.server.sql.DatabaseSetupService;


@Replace
public class TestDatabaseSetupService extends DatabaseSetupService {
	public void autoCreateDatabase() {
		//Dont do anything, this is just tests
	}
}
