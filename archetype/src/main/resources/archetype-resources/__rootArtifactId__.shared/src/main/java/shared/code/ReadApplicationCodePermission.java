#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import java.security.BasicPermission;

public class ReadApplicationCodePermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadApplicationCodePermission() {
		super(ReadApplicationCodePermission.class.getSimpleName());
	}
}
