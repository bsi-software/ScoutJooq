package com.acme.application.server.text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.common.text.ITextProviderService;

/**
 * Manages translated texts from the database. The Ordering of this service is lower than {@link TextProviderService}
 * which provides the default translations. Therefore, the application first tries to obtain a text from the database
 * text service and only afterward switches to the default.
 */
@Order(1000)
public class TextDbProviderService implements ITextProviderService {

  private Map<String, String> translationCache;
  private boolean cacheIsValid = false;

  @Override
  public String getText(Locale locale, String key, String... messageArguments) {
    checkCache();

    // try to get exact translation
    String localeId = TextService.convertLocale(locale);
    String text = translationCache.get(TextService.toId(localeId, key));
    if (StringUtility.hasText(text)) {
      return text;
    }

    // fall back to language (without country)
    if (locale != null) {
      String[] part = locale.toLanguageTag().split("[-_]");
      String localeIdLanguageOnly = part[0];
      text = translationCache.get(TextService.toId(localeIdLanguageOnly, key));

      if (StringUtility.hasText(text)) {
        return text;
      }
    }

    // fall back to default translation
    return translationCache.get(TextService.toId(null, key));
  }

  @Override
  public Map<String, String> getTextMap(Locale locale) {
    checkCache();

    return null;
  }

  public void invalidateCache() {
	  cacheIsValid = false;
  }
  
  public Map<Locale, String> getTexts(String key) {
    checkCache();

    Map<Locale, String> texts = new HashMap<Locale, String>();

    translationCache.keySet()
        .stream()
        .forEach(textId -> {
          if (key.equals(TextService.toKey(textId))) {
            String text = translationCache.get(textId);
            
            if(StringUtility.hasText(text)) {
                String locale = TextService.toLocale(textId);
            	texts.put(TextService.convertLocale(locale), text);
            }
          }
        });

    return texts;
  }

  /**
   * Loads text from repository if it is not valid.
   */
  private void checkCache() {
    if (cacheIsValid) {
      return;
    }

    translationCache = new HashMap<>();

    BEANS.get(TextService.class)
    	.getAll()
        .stream()
        .forEach(record -> {
        	String id = TextService.toId(record.getLocale(), record.getKey());
        	String translation = record.getText();
        	
        	translationCache.put(id, translation);
        	});

    cacheIsValid = true;
  }
}
