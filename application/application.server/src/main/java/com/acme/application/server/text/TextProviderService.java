package com.acme.application.server.text;

import org.eclipse.scout.rt.platform.text.AbstractDynamicNlsTextProviderService;

/**
 * Manages translated texts from the application's text property files. 
 */
public class TextProviderService extends AbstractDynamicNlsTextProviderService {

  @Override
  public String getDynamicNlsBaseName() {
    return "com.acme.application.nls.Texts";
  }
}
