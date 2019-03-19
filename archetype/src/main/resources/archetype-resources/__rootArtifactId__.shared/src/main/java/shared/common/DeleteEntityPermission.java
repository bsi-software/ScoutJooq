#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.common;

import java.security.BasicPermission;

public class DeleteEntityPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public DeleteEntityPermission() {
		super(DeleteEntityPermission.class.getSimpleName());
	}
}
