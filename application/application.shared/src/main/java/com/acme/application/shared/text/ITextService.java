package com.acme.application.shared.text;

import java.util.Map;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface ITextService extends IService {

	/**
	 * Returns the translated texts for all available locales of the provided text key.
	 */
	Map<String, String> getTexts(String textId);

	/**
	 * Persist the provided translated text.
	 */
	void addText(String key, String locale, String text);

	/**
	 * Returns the specified translated text.
	 */
	String getText(String key, String locale);

	/**
	 * Removes the translation for the provided key and locale from the persisted store.
	 */
	void deleteText(String key, String locale);

	void invalidateCache();

	TextTablePageData getTextTableData(SearchFilter filter);
}
