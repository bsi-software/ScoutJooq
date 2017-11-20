#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.shared.role;

import java.security.BasicPermission;

public class ReadPermissionPagePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadPermissionPagePermission() {
    super(ReadPermissionPagePermission.class.getSimpleName());
  }

}
