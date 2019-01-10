package com.acme.application.shared.payment;

import java.security.BasicPermission;

public class CreatePaymentPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreatePaymentPermission() {
		super(CreatePaymentPermission.class.getSimpleName());
	}
}
