package com.acme.application.shared.booking;

import java.security.BasicPermission;

public class ReadBookingPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadBookingPermission() {
		super(ReadBookingPermission.class.getSimpleName());
	}
}
