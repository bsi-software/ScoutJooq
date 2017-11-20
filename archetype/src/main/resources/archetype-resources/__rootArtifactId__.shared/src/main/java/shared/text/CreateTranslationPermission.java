#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.shared.text;

import java.security.BasicPermission;

public class CreateTranslationPermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public CreateTranslationPermission() {
    super(CreateTranslationPermission.class.getSimpleName());
  }
}
