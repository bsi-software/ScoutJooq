#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.text;

import org.eclipse.scout.rt.platform.text.AbstractDynamicNlsTextProviderService;

/**
 * Manages translated texts from the application's text property files. 
 */
public class TextProviderService extends AbstractDynamicNlsTextProviderService {

  @Override
  public String getDynamicNlsBaseName() {
    return "${package}.nls.Texts";
  }
}
