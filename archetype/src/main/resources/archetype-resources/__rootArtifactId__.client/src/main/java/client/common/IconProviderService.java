#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.common;

import java.net.URL;

import org.eclipse.scout.rt.client.services.common.icon.AbstractIconProviderService;
import org.eclipse.scout.rt.platform.Order;

import ${package}.client.ResourceBase;

/**
 * Provides application icons that are packaged with this application (resources folder). 
 */
@Order(2000)
public class IconProviderService extends AbstractIconProviderService {

  @Override
  protected URL findResource(String relativePath) {
    return ResourceBase.class.getResource("img/" + relativePath);
  }
}
