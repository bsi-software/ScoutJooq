#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.role;

import java.security.BasicPermission;

public class UpdateRolePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public UpdateRolePermission() {
    super(UpdateRolePermission.class.getSimpleName());
  }
}
