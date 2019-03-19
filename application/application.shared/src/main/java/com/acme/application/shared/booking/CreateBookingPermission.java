package com.acme.application.shared.booking;

import java.security.BasicPermission;

public class CreateBookingPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreateBookingPermission() {
		super(CreateBookingPermission.class.getSimpleName());
	}
}
