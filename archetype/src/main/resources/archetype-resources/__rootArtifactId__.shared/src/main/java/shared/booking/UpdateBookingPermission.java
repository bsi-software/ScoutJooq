#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.booking;

import java.security.BasicPermission;

public class UpdateBookingPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdateBookingPermission() {
		super(UpdateBookingPermission.class.getSimpleName());
	}
}
