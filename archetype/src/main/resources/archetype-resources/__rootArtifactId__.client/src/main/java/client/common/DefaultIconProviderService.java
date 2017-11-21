#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.common;

import java.net.URL;

import org.eclipse.scout.rt.client.services.common.icon.AbstractIconProviderService;

import ${package}.client.ResourceBase;

/**
 * <h3>{@link DefaultIconProviderService}</h3>
 *
 * @author mzi
 */
public class DefaultIconProviderService extends AbstractIconProviderService {
	@Override
	protected URL findResource(String relativePath) {
		return ResourceBase.class.getResource("/icons/" + relativePath);
	}
}
