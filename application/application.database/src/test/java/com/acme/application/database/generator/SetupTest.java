package com.acme.application.database.generator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.acme.application.database.or.core.tables.Code;
import com.acme.application.database.or.core.tables.Role;
import com.acme.application.database.or.core.tables.Text;
import com.acme.application.database.or.core.tables.Type;
import com.acme.application.database.or.core.tables.records.CodeRecord;
import com.acme.application.database.or.core.tables.records.RoleRecord;
import com.acme.application.database.or.core.tables.records.TextRecord;
import com.acme.application.database.or.core.tables.records.TypeRecord;
import com.acme.application.database.table.CodeTypeEnum;
import com.acme.application.database.table.RoleTable;
import com.acme.application.database.table.TableDataInitializer;
import com.acme.application.database.table.TextTable;

public class SetupTest {

	private Connection connection = null;

	@Before
	public void setup() throws Exception {
		try (Connection jdbcConnection = InitializerApplication.getConnection()) {
			InitializerApplication.initDatabase(jdbcConnection);
			connection = jdbcConnection;
		}
	}

	@After
	public void teardown() throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}

	@Test
	public void localeCodeTypeTest() throws TooManyRowsException, DataAccessException, SQLException {
		Type tt = Type.TYPE;
		TypeRecord type = getContext()
				.selectFrom(tt)
				.where(tt.ID.eq(TableDataInitializer.TYPE_ID_LOCALE))
				.fetchOne();

		Assert.assertNotNull("Type locale not found", type);
		Assert.assertEquals("Unexpected type", type.getCodeType(),  CodeTypeEnum.LOCALE.type());

		Code ct = Code.CODE;
		getContext()
		.selectFrom(ct)
		.where(ct.TYPE_ID.eq(type.getId()))
		.fetchStream()
		.forEach(code -> {
			Locale locale = Locale.forLanguageTag(code.getId());
			Assert.assertNotNull("Locale for code '" + code.getId() + "' not found", locale);

			Text xt = Text.TEXT;
			String localeDefault = TextTable.LOCALE_DEFAULT;
			TextRecord text = getContext()
					.selectFrom(xt)
					.where(xt.KEY.eq(code.getId()).and(xt.LOCALE.eq(localeDefault)))
					.fetchOne();

			Assert.assertNotNull("Locale name for code '" + code.getId() + "' and locale 'und' not found", text);
			String expectedText = locale.getDisplayName(Locale.forLanguageTag(localeDefault));

			if(text.getKey().equals("nn-NO")) {
				System.out.println("special case. expected: " + expectedText + ", actual text:" + text.getText());
			}
			else {
				Assert.assertEquals("Unexpected name for code '\" + code.getId() + \"' and locale 'und'",  expectedText, text.getText());
			}
		});


	}

	@Test
	public void sexCodeTypeTest() {
		Type tt = Type.TYPE;
		TypeRecord type = getContext()
				.selectFrom(tt)
				.where(tt.ID.eq(TableDataInitializer.TYPE_ID_SEX))
				.fetchOne();

		Assert.assertNotNull("Type sex not found", type);
		Assert.assertEquals("Unexpected type", type.getCodeType(),  CodeTypeEnum.STRING.type());

		Code ct = Code.CODE;
		List<CodeRecord> genders = getContext()
				.selectFrom(ct)
				.where(ct.TYPE_ID.eq(type.getId()))
				.fetchStream()
				.collect(Collectors.toList());

		Assert.assertEquals("Unexpected number of genders", 1, genders.size());

		CodeRecord undefined = getContext()
				.selectFrom(ct)
				.where(ct.ID.eq(TableDataInitializer.CODE_UNDEFINED.getId()))
				.fetchOne();

		Assert.assertNotNull("Code 'U' not found", undefined);
		Assert.assertTrue("Code 'U' is inactive", undefined.getActive());

		Text xt = Text.TEXT;
		TextRecord undefinedText = getContext()
				.selectFrom(xt)
				.where(xt.KEY.eq(undefined.getId()).and(xt.LOCALE.eq(TextTable.LOCALE_DEFAULT)))
				.fetchOne();

		Assert.assertNotNull("Text for code 'U' not found", undefinedText);
		Assert.assertEquals("Unexpected text for code 'U'", TableDataInitializer.TEXT_UNDEFINED.getText(), undefinedText.getText());
	}

	@Test
	public void rootRoleTest() {

		String root = TableDataInitializer.ROLE_ROOT.getName();
		RoleRecord rootResult = getContext()
				.selectFrom(Role.ROLE)
				.where(Role.ROLE.NAME.eq(RoleTable.ROOT))
				.fetchAny();

		Assert.assertNotNull("root role not found", rootResult);
		Assert.assertEquals("unexpected root role id", root, rootResult.getName());
	}

	@Test
	public void inactiveRoleTest() {

		String guest = TableDataInitializer.ROLE_GUEST.getName();
		RoleRecord rootResult = getContext()
				.selectFrom(Role.ROLE)
				.where(Role.ROLE.ACTIVE.isFalse())
				.fetchAny();

		Assert.assertNotNull("guest role not found", rootResult);
		Assert.assertEquals("unexpected guest role id", guest, rootResult.getName());
	}


	@Test
	public void roleCountTest() {

		int count = getContext()
				.selectCount()
				.from(Role.ROLE)
				.fetchOne(0, int.class);

		// root, user, guest
		Assert.assertEquals("unexpected number of roles",  3, count);
	}

	protected DSLContext getContext() {
		try {
			if(connection == null) {
				connection = InitializerApplication.getConnection();
			}
			
			if(!connection.isValid(1)) {
				connection.close();
				connection = InitializerApplication.getConnection();
			}
			
			return InitializerApplication.getContext(connection);
		} 
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
