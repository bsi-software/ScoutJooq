package com.acme.application.server.text;

import java.util.List;
import java.util.Locale;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.application.database.or.core.tables.records.TextRecord;
import com.acme.application.database.table.TableDataInitializer;
import com.acme.application.database.table.TableUtility;
import com.acme.application.server.ServerSession;

@RunWith(ServerTestRunner.class)
@RunWithSubject("root")
@RunWithServerSession(ServerSession.class)
public class TextServiceTest {

	private TextService service = null;

	@Before
	public void initService() {
		service = BEANS.get(TextService.class);
		Assert.assertNotNull("Text service could not be resolved", service);
	}
	
	@Test
	public void testGetAll() {
		List<TextRecord> texts = service.getAll();

		// make sure texts are not empty
		Assert.assertTrue("no text returned", texts.size() >= 1);

		// make sure we have text root
		String key = TableDataInitializer.TEXT_USER.getKey();
		String locale = TextService.LOCALE_DEFAULT;
		String localeGerman = TextService.convertLocale(Locale.GERMAN);

		TextRecord rootText = new TextRecord(key, locale, "some translation");
		TextRecord rootTextGerman = new TextRecord(key, localeGerman, "some german translation");

		// TODO implement equals + hashcode based on primary key
		boolean foundText = false;
		boolean foundTextGerman = false;

		for(TextRecord text: texts) {
			String textKey = text.getKey();
			String textLocale = text.getLocale();

			if(textKey.equals(rootText.getKey()) && textLocale.equals(rootText.getLocale())) {
				foundText = true;
			}
			else if(textKey.equals(rootTextGerman.getKey()) && textLocale.equals(rootTextGerman.getLocale())) {
				foundTextGerman = true;
			}
		}

		Assert.assertTrue("no entry 'root'", foundText);
		Assert.assertTrue("no entry 'root' in german", foundTextGerman);
	}

	@Test
	public void testSave() {
		String locale = TextService.LOCALE_DEFAULT;
		String localeGerman = TextService.convertLocale(Locale.GERMAN);
		TextRecord textOne = new TextRecord(TableUtility.createId(), locale, "test text");
		TextRecord textTwo = new TextRecord(TableUtility.createId(), localeGerman, "test german text");

		String textOneId = TextService.toId(locale, textOne.getKey());
		String textTwoId = TextService.toId(TextService.convertLocale(Locale.GERMAN), textTwo.getKey());

		Assert.assertNull(service.get(textOneId));
		Assert.assertNull(service.get(textTwoId));

		service.store(textOne);
		service.store(textTwo);

		TextRecord resultText = service.get(textOneId);
		Assert.assertNotNull("result text should not be null", resultText);
		Assert.assertEquals("result text not matching expectation", textOne, resultText);
		Assert.assertEquals("bad text in result text", textOne.getText(), resultText.getText());

		resultText = service.get(textTwoId);
		Assert.assertNotNull("result text two should not be null", resultText);
		Assert.assertEquals("result text two not matching expectation", textTwo, resultText);
		Assert.assertEquals("bad text two in result text", textTwo.getText(), resultText.getText());

	}
}
