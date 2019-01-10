package com.acme.application.shared.payment;

import java.security.BasicPermission;

public class UpdatePaymentPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdatePaymentPermission() {
		super(UpdatePaymentPermission.class.getSimpleName());
	}
}
