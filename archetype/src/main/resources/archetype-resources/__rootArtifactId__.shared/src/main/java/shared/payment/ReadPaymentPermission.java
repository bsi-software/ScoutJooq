#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.payment;

import java.security.BasicPermission;

public class ReadPaymentPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadPaymentPermission() {
		super(ReadPaymentPermission.class.getSimpleName());
	}
}
