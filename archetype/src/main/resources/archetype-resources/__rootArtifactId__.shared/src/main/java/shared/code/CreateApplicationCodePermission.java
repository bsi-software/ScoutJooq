#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import java.security.BasicPermission;

public class CreateApplicationCodePermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreateApplicationCodePermission() {
		super(CreateApplicationCodePermission.class.getSimpleName());
	}
}
