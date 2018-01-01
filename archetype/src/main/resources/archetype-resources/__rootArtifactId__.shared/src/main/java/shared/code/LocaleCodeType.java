#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import java.util.Locale;

import org.eclipse.scout.rt.shared.services.common.code.ICode;

public class LocaleCodeType extends BaseCodeType {

	private static final long serialVersionUID = 1L;
	
	/**
	 * ID taken from class TableDataInitializer. 
	 * IMPORTANT: This ID links the code type to the database content
	 */
	public static final String ID = "af410ba5-b1a3-4181-a655-0bcfe9d53b78";
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	@Override
	public Class<? extends IApplicationCodeType> getCodeTypeClass() {
		return LocaleCodeType.class;
	}
	
	/**
	 * Convenience method that returns the locale for the provided language tag.
	 * Does not check if the provided corresponds to an existing code of this code type 
	 * and only works for cases implemented in {@link Locale${symbol_pound}forLanguageTag(String)}.
	 */
	public static Locale getLocale(String languageTag) {
		return Locale.forLanguageTag(languageTag);
	}
	
	/**
	 * Convenience method that directly works with a locale object to return the code.
	 * Only works for cases implemented in {@link Locale${symbol_pound}toLanguageTag()}.
	 */
	public ICode<String> getCode(Locale locale) {
		return getCode(locale.toLanguageTag());
	}
}
