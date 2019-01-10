#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.payment;

import java.security.BasicPermission;

public class CreatePaymentPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreatePaymentPermission() {
		super(CreatePaymentPermission.class.getSimpleName());
	}
}
