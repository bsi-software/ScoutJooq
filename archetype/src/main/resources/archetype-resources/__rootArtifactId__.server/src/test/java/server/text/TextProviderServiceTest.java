#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.text;

import java.util.Locale;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ${package}.database.or.core.tables.records.TextRecord;
import ${package}.database.table.RoleTable;
import ${package}.database.table.TableDataInitializer;
import ${package}.database.table.TableUtility;
import ${package}.database.table.TextTable;
import ${package}.server.ServerSession;

@RunWith(ServerTestRunner.class)
@RunWithSubject("root")
@RunWithServerSession(ServerSession.class)
public class TextProviderServiceTest {

	@Test
	public void testPropertyText() {
		String key = "AuthorizationFailed";
		String expectedText = "Permission denied";
		String text = TEXTS.get(key);
		
		Assert.assertNotNull("text for key 'AuthorizationFailed' not found", text);
		Assert.assertEquals("unexpected text for key 'AuthorizationFailed'", expectedText, text);
	}
	
	@Test
	public void testExistingDBTextRole() {
		String key = RoleTable.toTextKey(RoleTable.USER);
		String expectedText = TableDataInitializer.TEXT_USER.getText();
		String text = TEXTS.get(key);
		
		Assert.assertNotNull("text for key 'user' not found", text);
		Assert.assertEquals("unexpected text for key 'user'", expectedText, text);
		
		Locale localeGerman = Locale.GERMAN;
		String expectedTextGerman = TableDataInitializer.TEXT_USER_DE.getText();
		String textGerman = TEXTS.get(localeGerman, key);
		
		Assert.assertNotNull("german text for key 'user' not found", text);
		Assert.assertEquals("unexpected german text for key 'user'", expectedTextGerman, textGerman);
	}
	
	
	@Test
	public void testExistingDBTextCodes() {
		TextRecord fileCodeText = TableDataInitializer.TEXT_TYPE_FILE;
		String key = fileCodeText.getKey();
		Locale locale = TextService.convertLocale(fileCodeText.getLocale());
		String expectedText = fileCodeText.getText();
		String text = TEXTS.get(locale, key);
		
		Assert.assertNotNull("text not found", text);
		Assert.assertEquals("unexpected text found", expectedText, text);
		
		Locale localeGerman = Locale.GERMAN;
		String textGerman = TEXTS.get(localeGerman, key);
		
		Assert.assertNotNull("german text not found", text);
		Assert.assertEquals("unexpected german text found", expectedText, textGerman);
	}
	
	@Test
	public void testMissingDBText() {
		String key = TableUtility.createId();
		String expectedText = TableUtility.createValue();
		String expectedMissingText = "undefined text " + key;
		String locale = TextTable.LOCALE_DEFAULT;
		
		String text = TEXTS.get(key);
		Assert.assertNotEquals("did not expect text for new key", expectedMissingText, text);
		
		BEANS.get(TextService.class).addText(key, locale, expectedText);
		BEANS.get(TextService.class).invalidateCache();
		
		text = TEXTS.get(key);
		Assert.assertNotNull("new text not found", text);
		Assert.assertEquals("unexpected translation for new text", expectedText, text);
	}
}
