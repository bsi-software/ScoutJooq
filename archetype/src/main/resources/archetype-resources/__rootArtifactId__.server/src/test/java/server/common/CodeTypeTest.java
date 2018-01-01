#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.common;

import java.util.List;
import java.util.Locale;

import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ${package}.server.ServerSession;
import ${package}.shared.code.LocaleCodeType;
import ${package}.shared.code.SexCodeType;

@RunWithSubject("root")
@RunWith(ServerTestRunner.class)
@RunWithServerSession(ServerSession.class)
public class CodeTypeTest {

	@Test
	public void testMaleFemaleCode() {
		ICode<String> codeMale = CODES.getCode(SexCodeType.Male.class);
		Assert.assertNotNull("Male code not found", codeMale);
		Assert.assertEquals("Unexpected ID for code type of male code", codeMale.getCodeType().getId(), SexCodeType.ID);
		
		ICode<String> codeFemale = CODES.getCode(SexCodeType.Female.class);
		Assert.assertNotNull("Female code not found", codeFemale);
		Assert.assertEquals("Unexpected ID for code type of female code", codeFemale.getCodeType().getId(), SexCodeType.ID);
		
		SexCodeType codeType = (SexCodeType) codeMale.getCodeType();
		Assert.assertNotNull("Sex code type not found", codeMale);
		Assert.assertTrue("Unexpected code type", codeType instanceof SexCodeType);
		
		ICode<String> codeUnefined = codeType.getCode(("U"));
		Assert.assertNotNull("Undefined code not found", codeUnefined);
		
		List<? extends ICode<String>> codes = codeType.getCodes();
		Assert.assertTrue("Unexpected number of codes. Expected >= 3 but found " + codes.size(), codes.size() >= 3);
	}
	
	@Test
	public void testLocaleCodeType() {
		LocaleCodeType codeType = (LocaleCodeType) CODES.getCodeTypes(LocaleCodeType.class).get(0);
		Assert.assertNotNull("Locale code type not found", codeType);
		
		Locale canadaFrench = Locale.CANADA_FRENCH;
		String canadaFrenchTag = canadaFrench.toLanguageTag();
		ICode<String> canadaFrenchCode = codeType.getCode(canadaFrenchTag);
		Assert.assertNotNull("Canada (French) code not found", canadaFrenchCode);
		Assert.assertEquals("Unexpected language tag for Canada (French) found", canadaFrenchTag, canadaFrenchCode.getId());
		
		Assert.assertEquals("Unexpected Locale for tag 'Canada (French)'", canadaFrench, LocaleCodeType.getLocale(canadaFrenchTag));
		
		ICode<String> canadaFrenchCode2 = codeType.getCode(canadaFrench);
		Assert.assertNotNull("Canada (French) code not found", canadaFrenchCode2);
		Assert.assertEquals("Unexpected code for Canada (French)", canadaFrenchCode.getId(), canadaFrenchCode2.getId());
		
	}
}
