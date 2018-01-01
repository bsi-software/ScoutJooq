#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.role;

import java.security.BasicPermission;

public class ReadRolePagePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadRolePagePermission() {
    super(ReadRolePagePermission.class.getSimpleName());
  }

}
