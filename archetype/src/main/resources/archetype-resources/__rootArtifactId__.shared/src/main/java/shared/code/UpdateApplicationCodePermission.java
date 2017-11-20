#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import java.security.BasicPermission;

public class UpdateApplicationCodePermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdateApplicationCodePermission() {
		super(UpdateApplicationCodePermission.class.getSimpleName());
	}
}
