#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.user;

import java.security.BasicPermission;

public class ReadUserPermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadUserPermission() {
    super(ReadUserPermission.class.getSimpleName());
  }
}
