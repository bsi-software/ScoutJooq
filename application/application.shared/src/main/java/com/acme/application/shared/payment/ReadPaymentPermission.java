package com.acme.application.shared.payment;

import java.security.BasicPermission;

public class ReadPaymentPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadPaymentPermission() {
		super(ReadPaymentPermission.class.getSimpleName());
	}
}
