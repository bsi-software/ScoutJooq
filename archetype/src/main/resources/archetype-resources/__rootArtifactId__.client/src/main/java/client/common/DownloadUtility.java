#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadUtility {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadUtility.class);
	
	public static void download(File file) {
		try {
			byte [] content = Files.readAllBytes(file.toPath());
			download(file.getName(), content);
		} 
		catch (IOException e) {
			LOG.error("Failed to read file content from " + file.getAbsolutePath(), e);
		}
	}
	
	public static void download(String name, byte [] content) {
		download(new BinaryResource(name, content));
	}
	
	public static void download(BinaryResource resource) {
		ClientSessionProvider.currentSession()
		.getDesktop()
		.openUri(resource, OpenUriAction.DOWNLOAD);
	}
}
