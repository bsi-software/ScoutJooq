package com.acme.application.database.generator;

import org.junit.Test;

import com.acme.application.database.table.TableDataInitializer;

public class CreateIdTest {

	@Test
	public void createIds()  throws Exception {
		System.out.println(TableDataInitializer.createId());
		System.out.println(TableDataInitializer.createId());
		System.out.println(TableDataInitializer.createId());
		System.out.println(TableDataInitializer.createId());
	}
}
