package com.acme.application.database.table;

import java.util.UUID;

public class TableUtility {

    /**
     * Generates and returns a new code id.
     * Implemented based on {@link UUID#randomUUID()}.
     */
	public static String createId() {
		return UUID.randomUUID().toString();
	}

    /**
     * Generates and returns a new string value.
     * Implemented based on {@link UUID#randomUUID()}.
     */
	public static String createValue() {
		return UUID.randomUUID().toString();
	}
}
