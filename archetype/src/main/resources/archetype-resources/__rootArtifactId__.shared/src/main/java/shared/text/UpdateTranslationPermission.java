#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.text;

import java.security.BasicPermission;

public class UpdateTranslationPermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public UpdateTranslationPermission() {
    super(UpdateTranslationPermission.class.getSimpleName());
  }
}
