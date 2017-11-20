/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.docx4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings;
import org.docx4j.openpackaging.parts.SpreadsheetML.Styles;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.CTBooleanProperty;
import org.xlsx4j.sml.CTBorder;
import org.xlsx4j.sml.CTBorders;
import org.xlsx4j.sml.CTCellAlignment;
import org.xlsx4j.sml.CTCellStyle;
import org.xlsx4j.sml.CTCellStyleXfs;
import org.xlsx4j.sml.CTCellStyles;
import org.xlsx4j.sml.CTCellXfs;
import org.xlsx4j.sml.CTFill;
import org.xlsx4j.sml.CTFills;
import org.xlsx4j.sml.CTFont;
import org.xlsx4j.sml.CTFontSize;
import org.xlsx4j.sml.CTFonts;
import org.xlsx4j.sml.CTNumFmt;
import org.xlsx4j.sml.CTNumFmts;
import org.xlsx4j.sml.CTRElt;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSst;
import org.xlsx4j.sml.CTStylesheet;
import org.xlsx4j.sml.CTXf;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Col;
import org.xlsx4j.sml.Cols;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.SheetData;

/**
 * Adapter for xlsx (Excel) processing by docx4j library.
 *
 * @since 1.0.0
 */
public class XlsxAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(XlsxAdapter.class);

	/**
	 * Predefined excel styles
	 */
	public static enum PredefinedStyle {
		NUMBER_PRECISION,
		NUMBER,
		FORMAT_TIMESTAMP,
		FORMAT_DATE,
		PERCENTAGE,
		WRAP_TEXT,
		BOLD,
		BOLD_AND_WRAP_TEXT,
		TITLE;
	}

	private static final DateFormat ISO_8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

	static {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		ISO_8601_DATE_FORMAT.setTimeZone(tz);
	}

	private static final long NUMBER_FORMAT_TIMESTAMP = 203;
	private static final long NUMBER_FORMAT_DATE = 204;

	private static final String[] COLUMN_ALPHABET = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	private static final int NUMBER_OF_CHARACTERS_PER_COLUMN = 15;

	private SpreadsheetMLPackage m_workbook;
	private CTSst m_sharedStrings;
	private CTStylesheet m_stylesheet;

	/**
	 * Creates an empty workbook.
	 *
	 * @throws ProcessingException
	 */
	public XlsxAdapter() throws ProcessingException {
		initNew();
	}

	/**
	 * Opens the provided workbook (as {@link File}) or creates an empty one (if file is not set)
	 *
	 * @param file
	 * @throws ProcessingException
	 */
	public XlsxAdapter(File file) throws ProcessingException {
		if (file == null || !file.exists() || file.length() == 0) {
			initNew();
		}
		else {
			InputStream is;
			try {
				is = new FileInputStream(file);
			}
			catch (FileNotFoundException e) {
				throw new ProcessingException("Loading xlsx failed", e);
			}
			initLoad(is);
		}
	}

	/**
	 * Opens the provided workbook (as {@link InputStream}) or creates an empty one (if inputStream is not set)
	 *
	 * @param inputStream
	 * @throws ProcessingException
	 */
	public XlsxAdapter(InputStream inputStream) throws ProcessingException {
		if (inputStream == null) {
			initNew();
		}
		else {
			initLoad(inputStream);
		}
	}

	private void initNew() throws ProcessingException {
		try {
			Docx4jProperties.getProperties().setProperty("docx4j.Log4j.Configurator.disabled", "true");

			//Workbook:
			m_workbook = SpreadsheetMLPackage.createPackage();

			//Shared Strings:
			initNewSharedStrings();

			//Style sheet:
			initNewStylesheet();
		}
		catch (Exception e) {
			throw new ProcessingException("Creating xlsx failed", e);
		}
	}

	private void initLoad(InputStream is) throws ProcessingException {
		try {
			Docx4jProperties.getProperties().setProperty("docx4j.Log4j.Configurator.disabled", "true");

			//Workbook:
			m_workbook = (SpreadsheetMLPackage) SpreadsheetMLPackage.load(is);

			//Shared Strings:
			loadSharedStrings();

			//Style sheet:
			loadStylesheet();
		}
		catch (Exception e) {
			throw new ProcessingException("Loading xlsx failed", e);
		}
	}

	/**
	 * Returns the workbook.
	 *
	 * @return Workbook
	 */
	public SpreadsheetMLPackage getWorkbook() {
		return m_workbook;
	}

	/**
	 * Returns the style sheet
	 *
	 * @return CTStylesheet
	 */
	public CTStylesheet getStyleSheet() {
		return m_stylesheet;
	}

	/**
	 * @param value
	 * @return
	 */
	protected long addSharedString(String value) {
		for (int i = 0; i < m_sharedStrings.getSi().size(); i++) {
			CTRst item = m_sharedStrings.getSi().get(i);
			if (value.equals(item.getT() == null ? null : item.getT().getValue())) {
				return i;
			}
		}

		CTRst item = Context.getsmlObjectFactory().createCTRst();
		CTXstringWhitespace whitespaceValue = new CTXstringWhitespace();
		whitespaceValue.setValue(value);
		item.setT(whitespaceValue);
		m_sharedStrings.getSi().add(item);
		return m_sharedStrings.getSi().size() - 1;
	}

	private void loadSharedStrings() throws Docx4JException {
		SharedStrings sharedStrings = m_workbook.getWorkbookPart().getSharedStrings();
		if (sharedStrings != null) {
			m_sharedStrings = sharedStrings.getContents();
		}
		else {
			initNewSharedStrings();
		}
	}

	private void initNewSharedStrings() throws InvalidFormatException {
		SharedStrings shared = new SharedStrings(new PartName("/xl/sharedStrings.xml"));
		m_sharedStrings = Context.getsmlObjectFactory().createCTSst();
		shared.setJaxbElement(m_sharedStrings);
		m_workbook.getWorkbookPart().addTargetPart(shared);
	}

	private void loadStylesheet() throws Docx4JException {
		List<Styles> parts = getParts(m_workbook.getRelationshipsPart(), Styles.class);
		if (parts.size() == 0) {
			initNewStylesheet();
		}
		else if (parts.size() == 1) {
			m_stylesheet = parts.get(0).getContents();
		}
		else {
			throw new IllegalStateException("Unexpected Styles parts size: " + parts.size());
		}
	}

	private void initNewStylesheet() throws InvalidFormatException {
		Styles styles = new Styles(new PartName("/xl/styles.xml"));
		m_stylesheet = Context.getsmlObjectFactory().createCTStylesheet();
		styles.setJaxbElement(getStyleSheet());
		initStylesheet();
		m_workbook.getWorkbookPart().addTargetPart(styles);
	}

	/**
	 * Returns the sheet of the workbook.
	 *
	 * @return Worksheets
	 */
	public List<WorksheetPart> getSheets() {
		return getParts(m_workbook.getRelationshipsPart(), WorksheetPart.class);
	}

	/**
	 * Creates a new sheet with provided name.
	 *
	 * @param name
	 *          Name of new sheet
	 * @return Created sheet
	 * @throws ProcessingException
	 */
	public WorksheetPart createSheet(String name) throws ProcessingException {
		try {
			int size = getSheets().size();
			return m_workbook.createWorksheetPart(new PartName("/xl/worksheets/sheet" + (size + 1) + ".xml"), name, size + 1);
		}
		catch (Exception e) {
			throw new ProcessingException("Failed to create new sheet", e);
		}
	}

	/**
	 * Saves the current workbook to the provided file.
	 *
	 * @param file
	 *          File (xlsx)
	 * @throws ProcessingException
	 */
	public void save(File file) throws ProcessingException {
		try {
			Save saver = new Save(m_workbook);
			saver.save(new FileOutputStream(file));
		}
		catch (Exception e) {
			throw new ProcessingException("Failed to store xlsx file", e);
		}
	}

	/**
	 * Saves the current workbook to the provided file.
	 *
	 * @param outputStream
	 *          Output Stream
	 * @throws ProcessingException
	 */
	public void save(OutputStream outputStream) throws ProcessingException {
		try {
			Save saver = new Save(m_workbook);
			saver.save(outputStream);
		}
		catch (Docx4JException e) {
			throw new ProcessingException("Failed to store xlsx outputStream", e);
		}
	}

	/**
	 * Returns the row. Creates the row if it does not exist.
	 *
	 * @param sheet
	 *          Worksheet
	 * @param rowNum
	 *          Row number (0-based)
	 * @return Row
	 * @throws Docx4JException 
	 */
	public Row getRow(WorksheetPart sheet, long rowNum) throws Docx4JException {
		return getRow(sheet.getContents().getSheetData(), rowNum);
	}

	/**
	 * Returns the row. Creates the row if it does not exist.
	 *
	 * @param sheetData
	 *          Worksheet data
	 * @param rowNum
	 *          Row number (0-based)
	 * @return Row
	 */
	protected Row getRow(SheetData sheetData, long rowNum) {
		for (Row row : sheetData.getRow()) {
			if ((rowNum + 1) == row.getR()) {
				return row;
			}
		}

		Row row = Context.getsmlObjectFactory().createRow();
		row.setR(rowNum + 1);
		sheetData.getRow().add(row);
		return row;
	}

	/**
	 * Returns the cell. Creates the cell if it does not exist.
	 *
	 * @param row
	 *          Row object retrieved e.g. by {@link #getRow(WorksheetPart, long)}
	 * @param colNum
	 *          Column number (0-based)
	 * @return Cell
	 */
	public Cell getCell(Row row, int colNum) {
		final String columnLetter = columnIndexToLetterNotation(colNum);
		final String cellPosition = columnLetter + row.getR();
		for (Cell cell : row.getC()) {
			if ((cellPosition).equals(cell.getR())) {
				return cell;
			}
		}

		Cell cell = Context.getsmlObjectFactory().createCell();
		cell.setR(cellPosition);
		row.getC().add(cell);
		return cell;
	}

	/**
	 * Sets the value of the cell.
	 * <p>
	 * The type of the value is auto-detected and the cell type is modified accordingly.
	 *
	 * @param sheet
	 *          Worksheet
	 * @param rowNum
	 *          Row number (0-based)
	 * @param colNum
	 *          Column number (0-based)
	 * @param value
	 *          Value to set
	 * @return Cell
	 * @throws Docx4JException 
	 */
	public Cell setCellValue(WorksheetPart sheet, long rowNum, int colNum, Object value) throws ProcessingException, Docx4JException {
		SheetData sheetData = sheet.getContents().getSheetData();
		Row row = getRow(sheetData, rowNum);
		Cell cell = getCell(row, colNum);
		setCellValue(cell, value);
		return cell;
	}

	/**
	 * Sets the value of the cell.
	 * <p>
	 * The type of the value is auto-detected and the cell type is modified accordingly.
	 *
	 * @param rowNum
	 *          Row number (0-based)
	 * @param colNum
	 *          Column number (0-based)
	 * @param value
	 *          Value to set
	 * @return Cell
	 */
	public Cell setCellValue(Row row, int colNum, Object value) throws ProcessingException {
		Cell cell = getCell(row, colNum);
		setCellValue(cell, value);
		return cell;
	}

	/**
	 * Sets the value of the cell
	 *
	 * @param cell
	 * @param value
	 * @throws ProcessingException
	 */
	public void setCellValue(Cell cell, Object value) throws ProcessingException {
		if (value == null) {
			cell.setT(STCellType.INLINE_STR); // inline string
			cell.setIs(null);
		}
		else if (value instanceof Number) {
			PredefinedStyle format;
			if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
				format = PredefinedStyle.NUMBER_PRECISION;
			}
			else {
				format = PredefinedStyle.NUMBER;
			}

			cell.setS(getPredefinedStyleNumber(format));
			cell.setT(STCellType.N); // number
			cell.setV(((Number) value).toString());
		}
		else if (value instanceof Timestamp) {
			cell.setS(getPredefinedStyleNumber(PredefinedStyle.FORMAT_TIMESTAMP));
			cell.setV(ISO_8601_DATE_FORMAT.format(new Date(((Timestamp) value).getTime())));
			cell.setV(Double.toString(getExcelDate(new Date(((Timestamp) value).getTime()))));
		}
		else if (value instanceof Date) {
			cell.setS(getPredefinedStyleNumber(PredefinedStyle.FORMAT_DATE));
			cell.setV(ISO_8601_DATE_FORMAT.format((Date) value));
			cell.setV(Double.toString(getExcelDate((Date) value)));
		}
		else if (value instanceof Boolean) {
			cell.setT(STCellType.B); // boolean
			cell.setV(((Boolean) value) ? "1" : "0");
		}
		else if (value instanceof String && ((String) value).endsWith("%")) {
			cell.setS(getPredefinedStyleNumber(PredefinedStyle.PERCENTAGE));

			String s = (String) value;
			try {
				// remove % sign
				s = s.substring(0, s.length() - 1);
				// divide by 100 to gain correct percentage
				cell.setV(Double.toString(Double.valueOf(s) / 100));
			}
			catch (Exception e) {
				if (LOG.isInfoEnabled()) {
					LOG.info("item=" + value + " value=" + s, e);
				}
				cell.setV(Long.toString(addSharedString("'" + s)));
			}
		}
		else {
			String s = "" + value;
			if (s.contains("\n")) {
				cell.setS(getPredefinedStyleNumber(PredefinedStyle.WRAP_TEXT));
			}
			cell.setV(Long.toString(addSharedString(s)));
			cell.setT(STCellType.S);
		}
	}

	/**
	 * Gets the value of the cell with given coordinates in the given sheet.
	 *
	 * @param sheet
	 *          The sheet of the cell
	 * @param rowNum
	 *          row index (0 based)
	 * @param colNum
	 *          column index (0 based)
	 * @return The value of the corresponding cell.
	 */
	public String getCellValue(WorksheetPart sheet, long rowNum, int colNum) {
		SheetData sheetData = null;
		try {
			sheetData = sheet.getContents().getSheetData();
		} 
		catch (Docx4JException e) {
			LOG.error("Docx4JException", e);
		}
		
		Row row = getRow(sheetData, rowNum);
		return getCellValue(row, colNum);
	}

	/**
	 * Gets the value of the cell with the given index in the given row.
	 *
	 * @param row
	 *          The row
	 * @param colNum
	 *          the index of the cell (0 based).
	 * @return The value of the corresponding cell.
	 */
	public String getCellValue(Row row, int colNum) {
		return getCellValue(getCell(row, colNum));
	}

	/**
	 * Gets the value of the given cell.
	 *
	 * @param cell
	 *          The cell to get the value from
	 * @return the value of the given cell as {@link String}
	 */
	public String getCellValue(Cell cell) {
		String v = cell.getV();
		if (STCellType.S.equals(cell.getT())) {
			// shared string
			int index = Integer.valueOf(v).intValue();
			CTRst ctRst = m_sharedStrings.getSi().get(index);
			CTXstringWhitespace t = ctRst.getT();
			if (t != null) {
				return t.getValue();
			}
			else {
				List<CTRElt> r = ctRst.getR();
				if (CollectionUtility.hasElements(r)) {
					// partially formatted shared string
					// when not the full cell contains formatting but only parts of the cell content
					StringBuilder sb = new StringBuilder();
					for (CTRElt elt : r) {
						CTXstringWhitespace t2 = elt.getT();
						if (t2 != null) {
							sb.append(t2.getValue());
						}
					}
					return sb.toString();
				}
			}
		}
		return v;
	}

	/**
	 * Gets the zero based column index of the given cell.
	 *
	 * @param cell
	 *          The cell for which the column index should be calculated.
	 * @return the zero based column index.
	 */
	public static int getColumnIndex(Cell cell) {
		return letterNotationToColumnIndex(cell.getR());
	}

	/**
	 * Sets the style for the cell.
	 *
	 * @param cell
	 *          Cell
	 * @param style
	 *          Predefined style
	 */
	public void setCellStyle(Cell cell, PredefinedStyle style) {
		cell.setS(getPredefinedStyleNumber(style));
	}

	/**
	 * Returns the internal style number for the predefined style.
	 *
	 * @param style
	 *          Predefined style.
	 * @return Internal style number
	 */
	public Long getPredefinedStyleNumber(PredefinedStyle style) {
		for (int i = 0; i < getStyleSheet().getCellStyles().getCellStyle().size(); i++) {
			CTCellStyle cellStyle = getStyleSheet().getCellStyles().getCellStyle().get(i);
			if (style.name().equals(cellStyle.getName())) {
				return (long) i;
			}
		}
		return null;
	}

	/**
	 * Initialized the predefined cell styles.
	 */
	protected final void initStylesheet() {
		initFonts();
		initFills();
		initBorders();
		initNumberFormats();
		initStyles();
	}

	/**
	 * Initializes the fonts.
	 */
	protected final void initFonts() {
		// font
		CTFonts fonts = Context.getsmlObjectFactory().createCTFonts();
		CTFont font;

		// default (font id 0)
		font = Context.getsmlObjectFactory().createCTFont();
		fonts.getFont().add(font);

		// bold (font id 1)
		font = Context.getsmlObjectFactory().createCTFont();
		fonts.getFont().add(font);
		CTBooleanProperty boldProperty = Context.getsmlObjectFactory().createCTBooleanProperty();
		boldProperty.setVal(Boolean.TRUE);
		font.getNameOrCharsetOrFamily().add(Context.getsmlObjectFactory().createCTFontB(boldProperty));

		// font size 24 (font id 2)
		font = Context.getsmlObjectFactory().createCTFont();
		fonts.getFont().add(font);
		CTFontSize fontSize = Context.getsmlObjectFactory().createCTFontSize();
		fontSize.setVal(24.0);
		font.getNameOrCharsetOrFamily().add(Context.getsmlObjectFactory().createCTFontSz(fontSize));

		getStyleSheet().setFonts(fonts);
	}

	/**
	 * Initializes the fills.
	 */
	protected final void initFills() {
		CTFills fills = Context.getsmlObjectFactory().createCTFills();
		CTFill fill = Context.getsmlObjectFactory().createCTFill();
		fills.getFill().add(fill);
		getStyleSheet().setFills(fills);
	}

	/**
	 * Initializes the borders.
	 */
	protected final void initBorders() {
		CTBorders borders = Context.getsmlObjectFactory().createCTBorders();
		CTBorder border = Context.getsmlObjectFactory().createCTBorder();
		borders.getBorder().add(border);
		getStyleSheet().setBorders(borders);
	}

	/**
	 * Initializes the number formats.
	 */
	protected final void initNumberFormats() {
		// number formats
		CTNumFmts numFmts = Context.getsmlObjectFactory().createCTNumFmts();
		CTNumFmt numFmt = null;

		numFmt = Context.getsmlObjectFactory().createCTNumFmt();
		numFmt.setNumFmtId(NUMBER_FORMAT_TIMESTAMP);
		numFmt.setFormatCode("dd.MM.yyyy hh:mm");
		numFmts.getNumFmt().add(numFmt);

		numFmt = Context.getsmlObjectFactory().createCTNumFmt();
		numFmt.setNumFmtId(NUMBER_FORMAT_DATE);
		numFmt.setFormatCode("dd.MM.yyyy");
		numFmts.getNumFmt().add(numFmt);

		getStyleSheet().setNumFmts(numFmts);
	}

	/**
	 * Initializes the styles.
	 */
	protected final void initStyles() {
		CTXf xf;

		CTCellStyleXfs cellStyleXfs = Context.getsmlObjectFactory().createCTCellStyleXfs();

		// default (0)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// number precision (1)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(4L); // �18.8.30 id: 4
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// number (2)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(3L); // �18.8.30 id: 3
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// timestamp (3)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(NUMBER_FORMAT_TIMESTAMP);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// date (4)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(NUMBER_FORMAT_DATE);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// precent (5)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(10L); // �18.8.30 id: 10
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// wrap text (6)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// bold (7)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(1L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// bold & text wrap (8)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(1L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		// title (font size 24) (9)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(2L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellStyleXfs.getXf().add(xf);

		getStyleSheet().setCellStyleXfs(cellStyleXfs);

		CTCellXfs cellXfs = Context.getsmlObjectFactory().createCTCellXfs();

		// default (xf id 0)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// number precision (xf id 1)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(4L); // �18.8.30 id: 4
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// number (xf id 2)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(3L); // �18.8.30 id: 3
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// timestamp (xf id 3)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(NUMBER_FORMAT_TIMESTAMP);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// date (xf id 4)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(NUMBER_FORMAT_DATE);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// precent (xf id 5)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(10L); // �18.8.30 id: 10
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// wrap text (xf id 6)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(0L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		xf.setApplyAlignment(Boolean.TRUE);
		CTCellAlignment alignment = Context.getsmlObjectFactory().createCTCellAlignment();
		alignment.setWrapText(Boolean.TRUE);
		xf.setAlignment(alignment);
		cellXfs.getXf().add(xf);

		// bold (xf id 7)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(1L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		// bold & text wrap (xf id 8)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(1L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		xf.setApplyAlignment(Boolean.TRUE);
		alignment = Context.getsmlObjectFactory().createCTCellAlignment();
		alignment.setWrapText(Boolean.TRUE);
		xf.setAlignment(alignment);
		cellXfs.getXf().add(xf);

		// title (xf id 9)
		xf = Context.getsmlObjectFactory().createCTXf();
		xf.setNumFmtId(0L);
		xf.setFontId(2L);
		xf.setFillId(0L);
		xf.setBorderId(0L);
		xf.setXfId(0L);
		cellXfs.getXf().add(xf);

		getStyleSheet().setCellXfs(cellXfs);

		CTCellStyles cellStyles = Context.getsmlObjectFactory().createCTCellStyles();
		CTCellStyle cellStyle;

		// default
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName("default");
		cellStyle.setXfId(0L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// number precision
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.NUMBER_PRECISION.name());
		cellStyle.setXfId(1L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// number
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.NUMBER.name());
		cellStyle.setXfId(2L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// timestamp
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.FORMAT_TIMESTAMP.name());
		cellStyle.setXfId(3L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// date
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.FORMAT_DATE.name());
		cellStyle.setXfId(4L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// precent
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.PERCENTAGE.name());
		cellStyle.setXfId(5L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// wrap text
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.WRAP_TEXT.name());
		cellStyle.setXfId(6L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// column header (bold)
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.BOLD.name());
		cellStyle.setXfId(7L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// column header (bold & text wrap)
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.BOLD_AND_WRAP_TEXT.name());
		cellStyle.setXfId(8L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		// title (font size 24)
		cellStyle = Context.getsmlObjectFactory().createCTCellStyle();
		cellStyle.setName(PredefinedStyle.TITLE.name());
		cellStyle.setXfId(9L);
		cellStyle.setBuiltinId(0L);
		cellStyles.getCellStyle().add(cellStyle);

		getStyleSheet().setCellStyles(cellStyles);
	}

	/**
	 * Method to recursively retrieve objects of a specific class.
	 *
	 * @param rp
	 *          Relationship part
	 * @param clazz
	 *          Class of objects to retrieve
	 * @return
	 */
	protected final <T extends Part> List<T> getParts(RelationshipsPart rp, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		traverseRelationships(list, new HashSet<Part>(), rp, clazz);
		return list;
	}

	/**
	 * Helper method for {@link #getParts(RelationshipsPart, Class)}.
	 *
	 * @param list
	 *          Result list
	 * @param handled
	 *          Set of already handled parts
	 * @param rp
	 *          Relationship part
	 * @param clazz
	 *          Class of objects to retrieve
	 */
	protected final <T extends Part> void traverseRelationships(List<T> list, Set<Part> handled, RelationshipsPart rp, Class<T> clazz) {
		for (Relationship r : rp.getRelationships().getRelationship()) {
			if (r.getTargetMode() != null && r.getTargetMode().equals("External")) {
				continue;
			}

			Part part = rp.getPart(r);

			if (clazz.isAssignableFrom(part.getClass())) {
				list.add(clazz.cast(part));
			}

			if (handled.contains(part)) {
				continue;
			}
			handled.add(part);
			if (part.getRelationshipsPart() != null) {
				traverseRelationships(list, handled, part.getRelationshipsPart(), clazz);
			}
		}
	}

	/**
	 * Converts a numeric column index to a letter column index.
	 * <p>
	 * Examples: 0 = A, 1 = B, 25 = Z, 26 = AA, 27 = AB
	 *
	 * @param columnIndex
	 *          the numeric index of the column (zero based)
	 * @return String representing the column in letter notation
	 */
	public static String columnIndexToLetterNotation(int columnIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Invalid column " + columnIndex + " provided");
		}
		final int base = COLUMN_ALPHABET.length;

		String rightChar = COLUMN_ALPHABET[columnIndex % base];
		columnIndex = (columnIndex / base) - 1;
		if (columnIndex < 0) {
			return rightChar;
		}

		String middleChar = COLUMN_ALPHABET[columnIndex % base];
		columnIndex = (columnIndex / base) - 1;
		if (columnIndex < 0) {
			return middleChar + rightChar;
		}

		String leftChar = COLUMN_ALPHABET[columnIndex % base];
		columnIndex = (columnIndex / base) - 1;
		if (columnIndex < 0) {
			return leftChar + middleChar + rightChar;
		}

		throw new IllegalArgumentException("Invalid column " + columnIndex + " provided");
	}

	/**
	 * Converts the column letter notation to a zero based column index.
	 * <p>
	 * Examples: A = 0, AA = 26, CF23 = 83
	 * </p>
	 *
	 * @param letterNotation
	 *          The letter notation of the column or cell.
	 * @return the column index of the cell (zero based)
	 */
	public static int letterNotationToColumnIndex(String letterNotation) {
		int ret = 0;
		int pos = 0;
		for (int i = letterNotation.length() - 1; i >= 0; i--) {
			char c = Character.toUpperCase(letterNotation.charAt(i));
			if (c >= COLUMN_ALPHABET[0].charAt(0) && c <= COLUMN_ALPHABET[COLUMN_ALPHABET.length - 1].charAt(0)) {
				int val = c - COLUMN_ALPHABET[0].charAt(0) + 1;
				int pow = (int) Math.pow(COLUMN_ALPHABET.length, pos);
				ret += (pow * val);
				pos++;
			}
		}
		return ret - 1;
	}

	/**
	 * Converts a {@link java.util.Date} to a double representing the date in Excel notation.
	 *
	 * @param date
	 *          Date
	 * @return Excel representation of date
	 */
	public static double getExcelDate(Date date)
	{
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);

		int yr = cal.get(1);
		if (cal.get(1) < 1900) {
			throw new IllegalArgumentException("'year' must be 1900 or greater");
		}

		double fraction = (double) (((cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)) * 60 + cal.get(Calendar.SECOND)) * 1000 + cal.get(Calendar.MILLISECOND)) / 86400000D;
		cal.get(Calendar.HOUR_OF_DAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.get(Calendar.HOUR_OF_DAY);

		int yr1 = yr - 1;
		int leapDays = ((yr1 / 4 - yr1 / 100) + yr1 / 400) - 460;
		double value = fraction + (double) (365 * (yr - ('\u076C')) + leapDays + cal.get(Calendar.DAY_OF_YEAR));

		if (value >= 60D) {
			value++;
		}
		return value;
	}

	/**
	 * Fills the csv into the current excel sheet.
	 *
	 * @param startRowIdx
	 *          Row to start with
	 * @param startColIdx
	 *          Column to start with
	 * @param title
	 *          Title
	 * @param infoRowsBefore
	 *          Rows that should be added before the csv data
	 * @param infoRowsAfter
	 *          Rows that should be added after the csv data
	 * @param csv
	 *          csv data
	 * @param containsColumnNames
	 *          {@code true} if the csv contains the column headers
	 * @throws ProcessingException
	 */
	public void fillCSV(int startRowIdx, int startColIdx, String title, String[] infoRowsBefore, String[] infoRowsAfter, Object[][] csv, boolean containsColumnNames) throws ProcessingException {
		int excelRowIndex = startRowIdx;
		int excelColIndex = startColIdx;
		int columnCount = 0;
		if (csv.length > 0) {
			columnCount = csv[0].length;
		}
		// while filling, disable user input and screen update
		WorksheetPart sheet;
		List<WorksheetPart> sheets = getSheets();
		if (sheets.size() <= 0) {
			sheet = createSheet("Sheet1");
		}
		else {
			sheet = sheets.get(0);
		}
		try {
			//
			// display title if applicable
			if (title != null) {
				// do nothing, set title at the end to have nice column auto fit
				excelRowIndex++;
				excelRowIndex++;
			}
			// reserve info rows space
			int infoRowsBeforeIndex = -1;
			if (infoRowsBefore != null && infoRowsBefore.length > 0) {
				infoRowsBeforeIndex = excelRowIndex;
				excelRowIndex += infoRowsBefore.length;
				excelRowIndex++;
			}

			int csvRowIndex = 0;

			// acquire here, otherwise excel fails to load sheet
			final Row startRow = getRow(sheet, startRowIdx);

			// display all columns
			if (containsColumnNames) {
				Row row = getRow(sheet, excelRowIndex);
				for (int c = 0; c < columnCount; c++) {
					String name = (String) csv[csvRowIndex][c];
					if (name != null) {

						Cell cell = setCellValue(row, excelColIndex + c, name);
						if (name.contains("\n")) {
							cell.setS(getPredefinedStyleNumber(PredefinedStyle.BOLD_AND_WRAP_TEXT));
						}
						else {
							cell.setS(getPredefinedStyleNumber(PredefinedStyle.BOLD));
						}
					}
				}
				// make columns bold
				excelRowIndex++;
				csvRowIndex++;
			}

			// display all rows
			try {
				while (csvRowIndex < csv.length) {
					Row row = getRow(sheet, excelRowIndex);
					
					for (int c = 0; c < columnCount; c++) {
						Object value = csv[csvRowIndex][c];
						setCellValue(row, c, value);
					}// end for col
					// next
					excelRowIndex++;
					csvRowIndex++;
				}// end for row
			}
			catch (Exception je) {
				throw new ProcessingException("interrupted", je);
			}

			// fit columns
			Cols cols = Context.getsmlObjectFactory().createCols();
			final int maximumDigitWidth = 7; // Calibiri, 11 point, 96 dpi
			for (int i = 0; i < excelColIndex + columnCount; i++) {
				// width = Truncate([{Number of Characters} * {Maximum Digit Width} + {5 pixel padding}]/{Maximum Digit Width}*256)/256
				double width = Math.floor((NUMBER_OF_CHARACTERS_PER_COLUMN * maximumDigitWidth + 5) / maximumDigitWidth * 256) / 256;
				cols.getCol().add(createBestFitColumn(i, width));
			}
			sheet.getContents().getCols().add(cols);

			//
			// set title of report now
			if (title != null) {
				Cell cell = setCellValue(startRow, startColIdx, title);
				cell.setS(getPredefinedStyleNumber(PredefinedStyle.TITLE));
			}
			// write infoRows before
			if (infoRowsBeforeIndex >= 0 && infoRowsBefore != null) {
				int r = infoRowsBeforeIndex;
				for (String s : infoRowsBefore) {
					setCellValue(sheet, r, excelColIndex, s);
					r++;
				}
			}
			// write infoRows after
			if (infoRowsAfter != null && infoRowsAfter.length > 0) {
				excelRowIndex++;
				int r = excelRowIndex;
				for (String s : infoRowsAfter) {
					setCellValue(sheet, r, excelColIndex, s);
					r++;
				}
			}
		}
		catch (Exception e) {
			throw new ProcessingException("unable execute operation", e);
		}
	}

	/**
	 * Creates a best fit column.
	 *
	 * @param columnIndex
	 *          Column index
	 * @param width
	 *          Column width
	 * @return Created column
	 */
	protected Col createBestFitColumn(int columnIndex, double width) {
		Col col = Context.getsmlObjectFactory().createCol();
		col.setBestFit(Boolean.TRUE);
		col.setCustomWidth(Boolean.TRUE);
		col.setMin(columnIndex + 1);
		col.setMax(columnIndex + 1);
		col.setWidth(width);
		return col;
	}

}
