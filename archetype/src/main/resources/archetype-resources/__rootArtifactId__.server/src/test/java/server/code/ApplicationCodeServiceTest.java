#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server.code;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.ObjectUtility;
import org.eclipse.scout.rt.shared.services.common.code.CodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ${package}.database.or.core.tables.records.CodeRecord;
import ${package}.database.table.TableDataInitializer;
import ${package}.database.table.TableUtility;
import ${package}.server.ServerSession;
import ${package}.shared.code.ApplicationCodeUtility;
import ${package}.shared.code.IApplicationCodeType;
import ${package}.shared.code.LocaleCodeType;
import ${package}.shared.code.SexCodeType;

@RunWithSubject("root")
@RunWith(ServerTestRunner.class)
@RunWithServerSession(ServerSession.class)
public class ApplicationCodeServiceTest {


	private ApplicationCodeService service = null;

	@Before
	public void initService() {
		service = BEANS.get(ApplicationCodeService.class);
		assertNotNull("Application code service could not be resolved", service);
	}
	
	@Test
	public void testDynamicSexCodes() {
		List<CodeRecord> codes = service.getCodeRecords(SexCodeType.ID);
		CodeRecord mCode = TableDataInitializer.CODE_MALE;
		CodeRecord fCode = TableDataInitializer.CODE_FEMALE;
		CodeRecord uCode = TableDataInitializer.CODE_UNDEFINED;

		// check codes loaded from database
		assertNotNull("Should not be null: codes", codes);
		assertTrue("At least one code should have been returned", codes.size() >= 1);
		assertFalse("Sex code 'M' found unexpectedly", codes.contains(mCode));
		assertFalse("Sex code 'F' found unexpectedly", codes.contains(fCode));
		assertTrue("Sex code 'U' not found", codes.contains(uCode));
	}
	
	@Test
	public void testUpdateDynamicSexCodes() {
		// get old record
		int codes = service.getCodeRecords(SexCodeType.ID).size();
		CodeRecord uCode = TableDataInitializer.CODE_UNDEFINED;		
		CodeRecord uRecord = service.getCodeRecord(uCode.getTypeId(), uCode.getId());
		assertNotNull("Failed to read 'U' code", uRecord);
		String valueOld = ObjectUtility.nvl(uRecord.getValue(), "null");
		
		// update record and get new copy
		String valueNew = TableUtility.createValue();
		uRecord.setValue(valueNew);
		service.store(uRecord);
		CodeRecord uRecordNew = service.getCodeRecord(uCode.getTypeId(), uCode.getId());
		int codesNew = service.getCodeRecords(SexCodeType.ID).size();
		
		// do tests
		assertNotNull("Failed to read 'U' code again", uRecordNew);
		assertNotEquals("Expected different values", valueOld, uRecordNew.getValue());
		assertEquals("Expected matching values", valueNew, uRecordNew.getValue());
		assertEquals("Expected same number of dynamic codes", codes, codesNew);
	}
	
	@Test
	public void testAddDynamicSexCode() {
		int codes = service.getCodeRecords(SexCodeType.ID).size();
		String codeId = TableUtility.createId();
		CodeRecord newCode = new CodeRecord(codeId, SexCodeType.ID, Double.valueOf(0), null, null, true);
		CodeRecord missingCode = service.getCodeRecord(newCode.getTypeId(), newCode.getId());		
		assertNull("New code should not be here already", missingCode);
		
		service.store(newCode);
		CodeRecord foundCode = service.getCodeRecord(newCode.getTypeId(), newCode.getId());		
		assertNotNull("New code is missing", foundCode);
		
		int codesNew = service.getCodeRecords(SexCodeType.ID).size();
		assertEquals("Expected increased number of dynamic codes", codes + 1, codesNew);
	}
	
	@Test
	public void testStoreDynamicCode() {
		List<ICode<String>> codes = ApplicationCodeUtility.getCodes(SexCodeType.class);
		assertNotNull("Null returned instead of code list", codes);
		assertTrue("Expected 3 or more codes but only found " + codes.size(), codes.size() >= 3);
		
		IApplicationCodeType codeType = ApplicationCodeUtility.getCodeType(SexCodeType.class);
		assertNotNull("Null returned instead of code type", codeType);
		
		// create new code
		String newCodeId = ApplicationCodeUtility.generateCodeId();
		String newCodeText = ApplicationCodeUtility.generateCodeId();
		CodeRow<String> newCodeRow = new CodeRow<String>(newCodeId, newCodeText).withActive(false);
		
		// the following uses the ApplicationCodeService to store the new code in the database
		codeType.store(newCodeRow);
		
		// reload codes and make sure the new code has become available
		ApplicationCodeUtility.reload(SexCodeType.class);
		ICode<String> newCode = ApplicationCodeUtility.getCode(SexCodeType.class, newCodeId);
		assertNotNull("New code not found", newCode);
		assertEquals("Code id mismatch", newCodeRow.getKey(), newCode.getId());
		assertEquals("Unexpected code text", newCodeRow.getText(), newCode.getText());
		assertEquals("Unexpected code active flag", newCodeRow.isActive(), newCode.isActive());
	}
	
	
	@Test
	public void testDynamicLocaleCodes() {
		List<CodeRecord> codes = service.getCodeRecords(LocaleCodeType.ID);
		String geChTag = "de-CH";
		CodeRecord geChCode = new CodeRecord(geChTag, LocaleCodeType.ID, null, null, null, true);
		
		assertNotNull("Should not be null: codes", codes);
		assertTrue("At least 100 codes should have been returned", codes.size() >= 100);
		assertTrue("Locale code " + geChTag + " not found", codes.contains(geChCode));
	}
}
