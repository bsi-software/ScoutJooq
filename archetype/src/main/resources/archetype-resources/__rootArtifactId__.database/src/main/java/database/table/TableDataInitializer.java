#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.database.generator.IDataInitializer;
import ${package}.database.or.core.tables.records.BookingDocumentRecord;
import ${package}.database.or.core.tables.records.BookingRecord;
import ${package}.database.or.core.tables.records.CodeRecord;
import ${package}.database.or.core.tables.records.DocumentRecord;
import ${package}.database.or.core.tables.records.PersonRecord;
import ${package}.database.or.core.tables.records.RolePermissionRecord;
import ${package}.database.or.core.tables.records.RoleRecord;
import ${package}.database.or.core.tables.records.TextRecord;
import ${package}.database.or.core.tables.records.TypeRecord;
import ${package}.database.or.core.tables.records.UserRecord;
import ${package}.database.or.core.tables.records.UserRoleRecord;
import ${package}.shared.code.FileCodeType;
import ${package}.shared.code.LocaleCodeType;
import ${package}.shared.code.SexCodeType;
import ${package}.shared.common.DateTimeUtility;
import ${package}.shared.security.PasswordUtility;

public class TableDataInitializer extends TableUtility implements IDataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(TableDataInitializer.class);

	//--- Core Schema Items ------------------------------------------------------------//
	public static final String TYPE_ID_LOCALE = LocaleCodeType.ID;
	public static final String TYPE_ID_SEX = SexCodeType.ID;
	public static final String TYPE_ID_FILE = FileCodeType.ID;

	public static final TypeRecord TYPE_LOCALE = new TypeRecord(TYPE_ID_LOCALE, CodeTypeEnum.LOCALE.type());
	public static final TypeRecord TYPE_FILE = new TypeRecord(TYPE_ID_FILE, CodeTypeEnum.STRING.type());
	public static final TypeRecord TYPE_SEX = new TypeRecord(TYPE_ID_SEX, CodeTypeEnum.STRING.type());

	// Male/female code ID's need to match with Java code
	public static final CodeRecord CODE_FEMALE = new CodeRecord("F", TYPE_ID_SEX, 10.0, null, null, true);
	public static final CodeRecord CODE_MALE = new CodeRecord("M", TYPE_ID_SEX, 20.0, null, null, true);
	public static final CodeRecord CODE_UNDEFINED = new CodeRecord("U", TYPE_ID_SEX, 30.0, null, null, true);

	public static final PersonRecord PERSON_ROOT = new PersonRecord("8be7647c-a5d4-408f-b7b6-7b36461b56c4", "Root", "", CODE_MALE.getId(), true);
	public static final PersonRecord PERSON_ALICE = new PersonRecord("368a17b5-eda6-4d98-ae89-114e6244736c", "Alice", "", CODE_FEMALE.getId(), true);
	public static final PersonRecord PERSON_BOB = new PersonRecord("5cb35b19-a17c-40fa-834e-c6a9222480be", "Bob", "", CODE_MALE.getId(), true);
	public static final PersonRecord PERSON_RABBIT = new PersonRecord("5cb35b19-a17c-40fa-834e-c6a9222480bf", "Rabbit", "", CODE_UNDEFINED.getId(), true);

	public static final UserRecord USER_ROOT = new UserRecord(
			UserTable.ROOT,
			PERSON_ROOT.getId(),
			Locale.ENGLISH.toLanguageTag(),
			PasswordUtility.calculateEncodedPassword("eclipse"),
			true);

	public static final UserRecord USER_ALICE = new UserRecord(
			"alice",
			PERSON_ALICE.getId(),
			TextTable.LOCALE_DEFAULT,
			PasswordUtility.calculateEncodedPassword("test"),
			true);

	public static final UserRecord USER_RABBIT = new UserRecord(
			"rabbit",
			PERSON_RABBIT.getId(),
			TextTable.LOCALE_DEFAULT,
			PasswordUtility.calculateEncodedPassword("hole"),
			true);

	public static final RoleRecord ROLE_ROOT = new RoleRecord(RoleTable.ROOT, RoleTable.ROOT, true);
	public static final RoleRecord ROLE_USER = new RoleRecord(RoleTable.USER, RoleTable.USER, true);
	public static final RoleRecord ROLE_GUEST = new RoleRecord(RoleTable.GUEST, RoleTable.GUEST, false);

	public static final UserRoleRecord USER_ROLE_ROOT = new UserRoleRecord(UserTable.ROOT, RoleTable.ROOT);
	public static final UserRoleRecord USER_ROLE_ALICE = new UserRoleRecord(USER_ALICE.getUsername(), RoleTable.USER);
	public static final UserRoleRecord USER_ROLE_RABBIT = new UserRoleRecord(USER_RABBIT.getUsername(), RoleTable.USER);

	public static byte[] DOCUMENT_README = "hello world".getBytes();
	public static String DOCUMENT_LOGO_NAME = "EclipseScout_Logo.png";
	public static String TIMESTAMP = DateTimeUtility.nowInUtcAsString();
	public static final DocumentRecord DOCUMENT_ALICE_1 = new DocumentRecord("a4e8fea9-e646-4e8e-897f-5f3b1d96202a", "Readme.txt", "txt", getSize(DOCUMENT_README), DOCUMENT_README, USER_ALICE.getUsername(), TIMESTAMP, true);
	public static final DocumentRecord DOCUMENT_ALICE_2 = new DocumentRecord("39060692-ad7f-4804-83fc-d16b1407379e", DOCUMENT_LOGO_NAME, "png", getSize(null), null, USER_ALICE.getUsername(), TIMESTAMP, true);

	public static final TextRecord TEXT_TYPE_LOCALE = new TextRecord(TYPE_ID_LOCALE, TextTable.LOCALE_DEFAULT, "Locale");
	public static final TextRecord TEXT_TYPE_FILE = new TextRecord(TYPE_ID_FILE, TextTable.LOCALE_DEFAULT, "File Type");
	public static final TextRecord TEXT_TYPE_SEX = new TextRecord(TYPE_ID_SEX, TextTable.LOCALE_DEFAULT, "Sex");

	public static final TextRecord TEXT_UNDEFINED = new TextRecord(CODE_UNDEFINED.getId(), TextTable.LOCALE_DEFAULT, "Undefined");

	public static final TextRecord TEXT_ROOT = new TextRecord(RoleTable.toTextKey(RoleTable.ROOT), TextTable.LOCALE_DEFAULT, "Root");
	public static final TextRecord TEXT_USER = new TextRecord(RoleTable.toTextKey(RoleTable.USER), TextTable.LOCALE_DEFAULT, "User");
	public static final TextRecord TEXT_GUEST = new TextRecord(RoleTable.toTextKey(RoleTable.GUEST), TextTable.LOCALE_DEFAULT, "Guest");

	public static final TextRecord TEXT_TYPE_FILE_DE = new TextRecord(TYPE_ID_FILE, TextTable.LOCALE_GERMAN, "Dateityp");
	public static final TextRecord TEXT_TYPE_SEX_DE = new TextRecord(TYPE_ID_SEX, TextTable.LOCALE_GERMAN, "Geschlecht");
	public static final TextRecord TEXT_USER_DE = new TextRecord(RoleTable.toTextKey(RoleTable.USER), Locale.GERMAN.toLanguageTag(), "Benutzer");
	public static final TextRecord TEXT_GUEST_DE = new TextRecord(RoleTable.toTextKey(RoleTable.GUEST), Locale.GERMAN.toLanguageTag(), "Gast");

	//----------------------------------------------------------------------------------//
	private int index;

	//----------------------------------------------------------------------------------//

	/**
	 * initialize data for all schemes
	 */
	@Override
	public void initialize(DSLContext context) {
		LOG.info("Insert minimal setup data");
		initializeCore(context);
	}

	/**
	 * insert minimal core data
	 */
	private void initializeCore(DSLContext ctx) {
		insertTexts(ctx);
		insertCodeTypes(ctx);
		insertPersons(ctx);
		insertUsers(ctx);
		insertRoles(ctx);
		insertUserRoles(ctx);
	}

	private void insertUserRoles(DSLContext ctx) {
		insert(ctx, USER_ROLE_ROOT);
	}

	private void insertRoles(DSLContext ctx) {
		insert(ctx, ROLE_ROOT);
		insert(ctx, ROLE_USER);
		insert(ctx, ROLE_GUEST);
	}

	private void insertUsers(DSLContext ctx) {
		insert(ctx, USER_ROOT);
	}

	private void insertPersons(DSLContext ctx) {
		insert(ctx, PERSON_ROOT);
	}

	/**
	 * Insert code types and codes.
	 * Only add dynamic codes. Codes that are already defined in the
	 * code type get overwritten by dynamic codes.
	 */
	private void insertCodeTypes(DSLContext ctx) {
		insertLocaleCodeType(ctx);
		insertFileCodeType(ctx);
		insertSexCodeType(ctx);
	}

	private void insertFileCodeType(DSLContext ctx) {
		insert(ctx, TYPE_FILE);
	}

	private void insertSexCodeType(DSLContext ctx) {
		insert(ctx, TYPE_SEX);
		insert(ctx, CODE_UNDEFINED);
	}

	private void insertLocaleCodeType(DSLContext ctx) {
		insert(ctx, TYPE_LOCALE);
		index = 0;

		Arrays.stream(Locale.getAvailableLocales())
		.forEach(locale -> {
			CodeRecord code = new CodeRecord(locale.toLanguageTag(), TYPE_ID_LOCALE, null, null, null, true);
			insert(ctx, code);
			index += 10;

			Locale locDefault = Locale.forLanguageTag(TextTable.LOCALE_DEFAULT);
			TextRecord text = new TextRecord(code.getId(), locDefault.toLanguageTag(), locale.getDisplayName(locDefault));
			insert(ctx, text);
		});
	}

	private void insertTexts(DSLContext ctx) {
		insert(ctx, TEXT_TYPE_LOCALE);
		insert(ctx, TEXT_TYPE_FILE);
		insert(ctx, TEXT_TYPE_FILE_DE);
		insert(ctx, TEXT_TYPE_SEX);
		insert(ctx, TEXT_TYPE_SEX_DE);

		insert(ctx, TEXT_UNDEFINED);

		insert(ctx, TEXT_ROOT);
		insert(ctx, TEXT_USER);
		insert(ctx, TEXT_USER_DE);
		insert(ctx, TEXT_GUEST);
		insert(ctx, TEXT_GUEST_DE);
	}

	@Override
	public void addSamples(DSLContext ctx) {
		LOG.info("Add sample data");
		addCoreSamples(ctx);
	}

	private void addCoreSamples(DSLContext ctx) {
		insertSamplePersons(ctx);
		insertSampleUsers(ctx);
		insertSampleRoles(ctx);
		insertSampleBookings(ctx);
	}

	private void insertSamplePersons(DSLContext ctx) {
		insert(ctx, PERSON_ALICE);
		insert(ctx, PERSON_BOB);
		insert(ctx, PERSON_RABBIT);
	}

	private void insertSampleRoles(DSLContext ctx) {
		insert(ctx, USER_ROLE_ALICE);
		insert(ctx, USER_ROLE_RABBIT);
	}

	private void insertSampleUsers(DSLContext ctx) {
		insert(ctx, USER_ALICE);
		insert(ctx, USER_RABBIT);
	}

	private void insertSampleBookings(DSLContext ctx) {
		UserRecord[] users = {USER_ALICE, USER_RABBIT, USER_ROOT};
		Instant dayOne = Year.now().atMonth(1).atDay(1).atTime(8, 0).atZone(ZoneId.systemDefault()).toInstant();
		for (int days = 0; days < 90; days++) {
			Instant from = dayOne.plus(days, ChronoUnit.DAYS);
			for (int i = 0; i < users.length; i++) {
				insertBookingWithUserAndDay(users[i], from, ctx);
			}
		}
	}

	private void insertBookingWithUserAndDay(UserRecord user, Instant day, DSLContext ctx) {
		BookingRecord bookingRecord = createBookingRecord(day, user);
		DocumentRecord documentRecord = createDocumentRecord(user);
		BookingDocumentRecord bookingDocumentRecord = createBookingDocumentRecord(bookingRecord, documentRecord);

		insert(ctx, bookingRecord);
		insert(ctx, documentRecord);
		insert(ctx, bookingDocumentRecord);
	}

	private BookingRecord createBookingRecord(Instant from, UserRecord user) {
		String id = createId();
		String userName = user.getUsername();
		Date dateFrom = Date.from(from);
		String dateDesc = new SimpleDateFormat("EEE, d MMM yyyy").format(dateFrom);
		String description = String.format("Work %s %s", userName, dateDesc);
		long hoursAdded = (long) new Random().nextInt(6);
		Date dateTo = Date.from(from.plus(hoursAdded, ChronoUnit.HOURS));
		String note = String.format("Note %s %s", userName, dateDesc);
		return new BookingRecord(id, description, dateFrom, dateTo, note, userName, Boolean.TRUE);
	}

	private DocumentRecord createDocumentRecord(UserRecord user) {
		String id = createId();
		String name = "Readme.txt";
		String fileExtension = "txt";
		BigDecimal size = getSize(DOCUMENT_README);
		String userName = user.getUsername();
		return new DocumentRecord(id, name, fileExtension, size, DOCUMENT_README, userName, TIMESTAMP, true);
	}

	private BookingDocumentRecord createBookingDocumentRecord(BookingRecord bookingRecord, DocumentRecord documentRecord) {
		String bookingRecordId = createId();
		String bookingId = bookingRecord.getId();
		String documentid = documentRecord.getId();
		return new BookingDocumentRecord(bookingRecordId, bookingId, documentid);
	}

	private void insert(DSLContext ctx, org.jooq.Record record) {
		try {

			// core records
			if(record instanceof CodeRecord) { ctx.executeInsert((CodeRecord)record); return; }
			if(record instanceof DocumentRecord) { ctx.executeInsert((DocumentRecord)record); return; }
			if(record instanceof PersonRecord) { ctx.executeInsert((PersonRecord)record); return; }
			if(record instanceof RolePermissionRecord) { ctx.executeInsert((RolePermissionRecord)record); return; }
			if(record instanceof RoleRecord) { ctx.executeInsert((RoleRecord)record); return; }
			if(record instanceof TextRecord) { ctx.executeInsert((TextRecord)record); return; }
			if(record instanceof TypeRecord) { ctx.executeInsert((TypeRecord)record); return; }
			if(record instanceof UserRoleRecord) { ctx.executeInsert((UserRoleRecord)record); return; }
			if(record instanceof UserRecord) { ctx.executeInsert((UserRecord)record); return; }
			if(record instanceof BookingRecord) { ctx.executeInsert((BookingRecord)record); return; }
			if(record instanceof BookingDocumentRecord) { ctx.executeInsert((BookingDocumentRecord)record); return; }

			LOG.warn("unknown record type '{}' in insert(). skipping insert ", record.getClass().getSimpleName());
		}
		catch(DataAccessException e) {
			/* NOP */
		}
	}

	private static BigDecimal getSize(byte [] content) {
		return content != null ? BigDecimal.valueOf(content.length) : BigDecimal.ZERO;
	}
}
