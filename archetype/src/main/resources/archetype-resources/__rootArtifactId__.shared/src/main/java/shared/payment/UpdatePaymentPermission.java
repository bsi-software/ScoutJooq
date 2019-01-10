#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.payment;

import java.security.BasicPermission;

public class UpdatePaymentPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdatePaymentPermission() {
		super(UpdatePaymentPermission.class.getSimpleName());
	}
}
