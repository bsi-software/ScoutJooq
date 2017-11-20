#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.shared.user;

import java.security.BasicPermission;

public class UpdateUserPermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public UpdateUserPermission() {
    super(UpdateUserPermission.class.getSimpleName());
  }
}
