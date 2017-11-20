#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSI CRM Software License v1.0
 * which accompanies this distribution as bsi-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.docx4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.structure.HeaderFooterPolicy;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTCompat;
import org.docx4j.wml.CTCompatSetting;
import org.docx4j.wml.CTMarkup;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.ProofErr;
import org.docx4j.wml.R;
import org.docx4j.wml.R.Tab;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.SdtContentBlock;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.util.FileUtility;
import org.eclipse.scout.rt.platform.util.FormattingUtility;
import org.eclipse.scout.rt.platform.util.ObjectUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class for creating and manipulating docx documents. The adapter is
 * designed to be used directly or to subclass, if specific behavior is missing.
 * <p/>
 * <b>Note:</b> Programmatically inserting text into a document should always make use of
 * {@link ${symbol_pound}insertTextInParagraph(ObjectFactory, RPr, P, int, String)}.
 *
 * @since 1.0.0
 */
public class DocxAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(DocxAdapter.class);

	static final String WORD_SCHEMA_NAMESPACE = "http://schemas.microsoft.com/office/word";
	static final String COMPATIBILITY_MODE_NAME = "compatibilityMode";
	static final String WORD_2010_INTERNAL_VERSION = "14";

	static final String UNABLE_EXECUTE_OPERATION = "unable execute operation";

	protected static final String PRESERVE_WHITESPACE = "preserve";

	public static final String XPATH_PARAGRAPHS_WITH_DOCVARIABLE_INSTR_TEXT = "//w:p[//w:instrText[contains(.,'DOCVARIABLE')]]";

	/** Pattern to match all unicode characters that are not valid according to the XML 1.0 specification. */
	protected static final Pattern XML_INVALID_CHARACTERS = Pattern.compile("[^" + "${symbol_escape}u0009${symbol_escape}r${symbol_escape}n" + "${symbol_escape}u0020-${symbol_escape}uD7FF" + "${symbol_escape}uE000-${symbol_escape}uFFFD" + "${symbol_escape}ud800${symbol_escape}udc00-${symbol_escape}udbff${symbol_escape}udfff" + "]");

	private WordprocessingMLPackage m_package;
	private File m_file;

	/**
	 * Creates an uninitialized docx adapter (used by subclasses the handle initialization manually).
	 */
	protected DocxAdapter() {
	}

	/**
	 * Create a word adapter that either opens an existing document (when template=false) or creates a new document using
	 * a template (when template=true).
	 *
	 * @param file
	 *          File to open
	 */
	public DocxAdapter(File file) throws ProcessingException {
		this(createPackageFromFile(file));
		m_file = file;
	}

	/**
	 * Protected constructor used for testing purposes or other constructors only.
	 */
	protected DocxAdapter(WordprocessingMLPackage wordMlPackage) {
		m_package = wordMlPackage;
		ensureCompatibilitySettings();
	}

	/**
	 * Creates am empty document package.
	 *
	 * @return
	 * @throws ProcessingException
	 */
	protected static WordprocessingMLPackage createEmptyPackage() throws ProcessingException {
		// create new document
		try {
			return WordprocessingMLPackage.createPackage();
		}
		catch (Docx4JException e) {
			throw new ProcessingException("Error creating an empty word package", e);
		}
	}

	/**
	 * Creates a package based on the file provided.
	 * <p>
	 * If the file does not exist an exception is thrown. If the file size is 0, an empty document is created.
	 *
	 * @param file
	 *          File
	 * @return Package
	 * @throws ProcessingException
	 */
	protected static WordprocessingMLPackage createPackageFromFile(File file) throws ProcessingException {
		Docx4jProperties.getProperties().setProperty("docx4j.Log4j.Configurator.disabled", "true");

		checkFile(file);

		// open given document
		if (file.length() == 0) {
			return createEmptyPackage();
		}

		try {
			return WordprocessingMLPackage.load(file);
		}
		catch (Docx4JException e) {
			if (!FileUtility.isZipFile(file)) {
				throw new VetoException("The document " + file.getName() + " is not an Office Open XML document (docx).", e);
			}
			throw new ProcessingException("unable to process word document", e);
		}
	}

	/**
	 * Checks if the provided file exists and can be read. Throws a processing exception otherwise.
	 *
	 * @param file
	 *          File to check for
	 * @throws ProcessingException
	 */
	protected static void checkFile(File file) throws ProcessingException {
		if (file == null || !file.exists() || !file.canRead()) {
			throw new ProcessingException("The template file does not exist or cannot be read '" + file + "'");
		}
	}

	public WordprocessingMLPackage getPackage() {
		return m_package;
	}

	protected void setPackage(WordprocessingMLPackage wordMlPackage) {
		m_package = wordMlPackage;
	}

	protected File getFile() {
		return m_file;
	}

	protected void setFile(File file) {
		this.m_file = file;
	}

	/**
	 * Replaces a single word document variable. The formatting is expected to be done before calling this method Warning.
	 *
	 * @param name
	 *          name of the document variable to be replaced
	 * @param value
	 *          a string the document variable is replaced with
	 */
	public void setField(String name, String value) throws ProcessingException {
		if (m_package == null || StringUtility.isNullOrEmpty(name)) {
			return;
		}
		setFields(Collections.singletonMap(name, value));
	}

	/**
	 * Replaces word document variables using the given map. Document variables that are not part of the map remain in the
	 * document. Document variables may appear in the main document or in any header or footer (default, first, even,
	 * odd).
	 *
	 * @param placeholderValues
	 *          map with document variables
	 * @throws ProcessingException
	 */
	public void setFields(Map<String, String> originalPlaceholderValues) throws ProcessingException {
		if (originalPlaceholderValues == null || originalPlaceholderValues.isEmpty()) {
			return;
		}

		final Map<String, String> placeholderValues = new HashMap<String, String>();
		placeholderValues.putAll(originalPlaceholderValues);
		cleanPlaceholderValues(placeholderValues);

		processComplexFields(XPATH_PARAGRAPHS_WITH_DOCVARIABLE_INSTR_TEXT, new IDocxComplexFieldProcessor() {
			@Override
			public boolean processField(P p, DocxComplexField complexField) {
				// compute doc variable name
				StringBuilder sb = new StringBuilder();
				RPr rpr = null;
				for (Object content : complexField.getCodeContent()) {
					if (content instanceof R) {
						R run = (R) content;
						if (rpr == null) {
							rpr = run.getRPr();
						}
						sb.append(getInstrTextValue(run));
					}
				}

				Pattern pattern = Pattern.compile("${symbol_escape}${symbol_escape}s*DOCVARIABLE${symbol_escape}${symbol_escape}s*(.*?)${symbol_escape}${symbol_escape}s*(?:${symbol_escape}${symbol_escape}${symbol_escape}${symbol_escape}${symbol_escape}${symbol_escape}* MERGEFORMAT)?${symbol_escape}${symbol_escape}s*");
				Matcher m = pattern.matcher(sb.toString());
				if (m.matches()) {
					String name = m.group(1);
					if (placeholderValues.containsKey(name)) {
						// remove doc variable-related children
						int insertPoint = p.getContent().indexOf(complexField.getBeginR());
						p.getContent().removeAll(complexField.getFieldDocumentNodes());
						// set new value
						insertTextInParagraph(new ObjectFactory(), rpr, p, insertPoint, placeholderValues.get(name));
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Remove non unicode characters which are not valid in XML 1.0.
	 */
	protected void cleanPlaceholderValues(Map<String, String> placeholderValues) {
		for (Entry<String, String> entry : placeholderValues.entrySet()) {
			String value = entry.getValue();

			if (value != null) {
				entry.setValue(cleanPlaceholderValue(value));
			}
		}
	}

	protected String cleanPlaceholderValue(String value) {
		if (value != null) {
			value = XML_INVALID_CHARACTERS.matcher(value).replaceAll(" ");
		}
		return value;
	}

	/**
	 * Transforms the given string into text and run objects and adds them to the given paragraph. Multiple paragraphs are
	 * added if the text contains more than one line. The method falls-back to breaks, if paragraphs can not be created.
	 * <p/>
	 * <b>Example:</b> A string is inserted at the position marked with <em>insertion point</em>.
	 *
	 * <pre>
	 * &lt;container&gt;
	 *   &lt;paragraph 1&gt;
	 *     &lt;!-- preceding-sibling --&gt;
	 *     &lt;!-- insertion point --&gt;
	 *     &lt;!-- following-sibling --&gt;
	 *   &lt;/paragraph 1&gt;
	 * &lt;/container&gt;
	 * </pre>
	 *
	 * The result contains new paragraph nodes if the given text has more than one line. The original paragraph is reused.
	 *
	 * <pre>
	 * &lt;container&gt;
	 *   &lt;paragraph 1&gt;
	 *     &lt;!-- preceding-sibling --&gt;
	 *     &lt;!-- first line of text to be inserted --&gt;
	 *   &lt;/paragraph 1&gt;
	 * 
	 *   &lt;paragraph 2&gt;
	 *     &lt;!-- second line of text to be inserted --&gt;
	 *   &lt;/paragraph 2&gt;
	 * 
	 *   [...]
	 * 
	 *   &lt;paragraph n&gt;
	 *     &lt;!-- last line of text to be inserted --&gt;
	 *     &lt;!-- following-sibling --&gt;
	 *   &lt;/paragraph n&gt;
	 * &lt;/container&gt;
	 * </pre>
	 *
	 * @param factory
	 *          object factory docx4j elements are created with.
	 * @param runProperties
	 *          run properties applied to every run created within this method.
	 * @param p
	 *          paragraph the text is added to.
	 * @param index
	 *          index within the given paragraph at which the specified element is
	 *          to be inserted
	 * @param s
	 *          text to be inserted at the given place
	 */
	protected void insertTextInParagraph(ObjectFactory factory, RPr runProperties, P p, int index, String s) {
		insertTextInParagraph(factory, runProperties, p, index, s, false);
	}

	protected void insertTextInParagraph(ObjectFactory factory, RPr runProperties, P p, int index, String s, boolean enforceBreaks) {
		if (StringUtility.getLineCount(s) == 1) {
			// a) the text has only one line.
			p.getContent().add(index, createTextRun(factory, runProperties, s));
			return;
		}

		// b) The text has more than one line, each of them is represented by a paragraph.
		// 1. remove following-siblings from paragraph so that they can be added after the last line of the document variable
		List<Object> followingSiblings = new ArrayList<Object>(p.getContent().subList(index, p.getContent().size()));
		p.getContent().removeAll(followingSiblings);
		boolean first = true;
		P currentP = p;

		for (String line : StringUtility.getLines(s)) {
			// 1. crate a run for the given line
			R run = createTextRun(factory, runProperties, line);
			if (first) {
				// the first run is just added in the existing paragraph.
				currentP.getContent().add(run);
				first = false;
				continue;
			}

			// 2.a create a new paragraph
			// A paragraph can occur in different environments. The following are supported by this class:
			// body (§17.2.2), ftr (§17.10.3), hdr (§17.10.4), tc (§17.4.66)
			// The following are not supported:
			// comment (§17.13.4.2), customXml (§17.5.1.6), endnote (§17.11.2), footnote (§17.11.10), sdtContent (§17.5.2.34)
			if (!enforceBreaks
					&& p.getParent() instanceof ContentAccessor
					&& (p.getParent() instanceof Body
							|| p.getParent() instanceof Ftr
							|| p.getParent() instanceof Hdr
							|| p.getParent() instanceof Tc
							|| p.getParent() instanceof SdtContentBlock)) {

				P newP = factory.createP();
				newP.setPPr(p.getPPr());
				newP.getContent().add(run);

				List<Object> parentContent = ((ContentAccessor) p.getParent()).getContent();
				parentContent.add(parentContent.indexOf(currentP) + 1, newP);
				newP.setParent(p.getParent());
				currentP = newP;
			}
			else {
				if (!enforceBreaks) {
					LOG.warn("unsupported parent element for a paragraph: " + p.getParent());
					// fall back and just add the run after a break in the last
					// successfully added paragraph
				}
				Br br = factory.createBr();
				run.getContent().add(0, br);
				currentP.getContent().add(run);
			}
		}

		// 3. add following siblings to current paragraph (i.e. the last paragraph)
		currentP.getContent().addAll(followingSiblings);
	}

	/**
	 * Creates a new text run for the given text.
	 * <p/>
	 * <b>Note:</b> This method uses the given string as-is, except that <code>null</code> is treated as the empty string.
	 * Multiline texts must be split by the caller into different paragraphs (see
	 * {@link ${symbol_pound}insertTextInParagraph(ObjectFactory, RPr, P, int, String)}). Additinally, tab characters are replaced by
	 * the corresponding tab element (i.e. &tl;w:tab /&gt;).
	 *
	 * @param factory
	 *          object factory docx4j elements are created with.
	 * @param runProperties
	 *          run properties applied to every run created within this method.
	 * @param s
	 *          text to be inserted at the given place
	 * @return
	 */
	protected R createTextRun(ObjectFactory factory, RPr runProperties, String s) {
		R run = factory.createR();
		run.setRPr(runProperties);

		String[] parts = StringUtility.split(ObjectUtility.nvl(s, ""), "${symbol_escape}${symbol_escape}t");
		if (parts.length == 0) {
			parts = new String[]{""};
		}
		boolean first = false;
		for (String textPart : parts) {
			if (!first) {
				first = true;
			}
			else {
				Tab tab = factory.createRTab();
				run.getContent().add(factory.createRTab(tab));
			}
			Text text = factory.createText();
			text.setValue(textPart);
			text.setSpace(PRESERVE_WHITESPACE);
			run.getContent().add(factory.createRT(text));
		}
		return run;
	}

	/**
	 * @return Returns the complex char field type contained in the given run or
	 *         null.
	 */
	protected STFldCharType getFldCharType(R r) {
		FldChar fldChar = getFldChar(r);
		if (fldChar == null) {
			return null;
		}
		return fldChar.getFldCharType();
	}

	/**
	 * @return Returns the complex char field contained in the given run or null.
	 */
	protected FldChar getFldChar(R r) {
		if (r == null || r.getContent() == null) {
			return null;
		}
		for (Object o : r.getContent()) {
			o = XmlUtils.unwrap(o);
			if (o instanceof FldChar) {
				return (FldChar) o;
			}
		}
		return null;
	}

	/**
	 * @return Returns the contents of the instrText element contained by the given run or the empty string.
	 */
	protected String getInstrTextValue(R r) {
		if (r == null || r.getContent() == null) {
			return "";
		}
		for (Object o : r.getContent()) {
			if (o instanceof JAXBElement<?>
			&& ObjectUtility.equals(((JAXBElement<?>) o).getName()
					.getLocalPart(), "instrText")) {
				o = XmlUtils.unwrap(o);
				if (o instanceof Text) {
					return ((Text) o).getValue();
				}
			}
		}
		return "";
	}

	/**
	 * sets the table indexed by count as current active
	 *
	 * @param index
	 *          vararg of table indexes
	 * @throws ProcessingException
	 */
	protected Tbl getTableByIndex(int... index) throws ProcessingException {
		return findTableByIndex(index);
	}

	/**
	 * sets the table indexed by bookmark as current active
	 *
	 * @param String
	 *          : name of bookmark
	 * @throws ProcessingException
	 */
	protected Tbl getTableByBookmark(String bookmark) throws ProcessingException {
		try {
			return findTableByBookmark(bookmark);
		}
		catch (JAXBException e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		}
	}

	/**
	 * Deletes the table referenced by the provided bookmark.
	 *
	 * @param bookmark
	 *          Bookmark of table
	 * @throws ProcessingException
	 */
	public void deleteTableByBookmark(String bookmark) throws ProcessingException {
		boolean changed = false;
		try {
			Tbl tbl = findTableByBookmark(bookmark);
			if (tbl == null) {
				return;
			}

			// first try to remove the complete table
			Object parent = XmlUtils.unwrap(tbl.getParent());
			if (parent instanceof ContentAccessor) {
				changed = ((ContentAccessor) parent).getContent().remove(tbl);
			}

			// if it did not work, remove table contents
			if (!changed) {
				for (Iterator<Object> it = tbl.getContent().iterator(); it
						.hasNext();) {
					Object next = XmlUtils.unwrap(it.next());
					if (next instanceof Tr) {
						it.remove();
					}
				}
			}
		}
		catch (JAXBException e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		}
		finally {
			if (changed) {
				applyChangesToJaxbElements();
			}
		}
	}

	/**
	 * Fills the specified table.
	 *
	 * @param tableIndex
	 *          Index of table to fill
	 * @param Object
	 *          [][]: data is a matrix
	 * @param int: startRow, row to start inserting values, 0 based
	 * @param int: startCol, column to start inserting values, 0 based expected
	 *        and handled value types are: {@link String} {@link java.util.date} is formatted using
	 *        {@link java.text.dateFormat${symbol_pound}MEDIUM} optionally
	 *        displaying time when time is other than 00:00:00 {@link Number} is
	 *        formatted using {@link java.text.numberFormat} {@link Boolean} is
	 *        formatted as "X" for true, "" for false
	 * @throws ProcessingException
	 */
	public void fillTable(int tableIndex, int startRow, int startCol, Object[][] data) throws ProcessingException {
		fillTable(getTableByIndex(tableIndex), startRow, startCol, data);
	}

	/**
	 * Fills the specified table.
	 *
	 * @param tableBookmark
	 *          Bookmark of table to fill
	 * @param Object
	 *          [][]: data is a matrix
	 * @param int: startRow, row to start inserting values, 0 based
	 * @param int: startCol, column to start inserting values, 0 based expected
	 *        and handled value types are: {@link String} {@link java.util.date} is formatted using
	 *        {@link java.text.dateFormat${symbol_pound}MEDIUM} optionally
	 *        displaying time when time is other than 00:00:00 {@link Number} is
	 *        formatted using {@link java.text.numberFormat} {@link Boolean} is
	 *        formatted as "X" for true, "" for false
	 * @throws ProcessingException
	 */
	public void fillTable(String tableBookmark, int startRow, int startCol, Object[][] data) throws ProcessingException {
		fillTable(getTableByBookmark(tableBookmark), startRow, startCol, data);
	}

	/**
	 * fills the currently active table
	 *
	 * @param Object
	 *          [][]: data is a matrix
	 * @param int: startRow, row to start inserting values, 0 based
	 * @param int: startCol, column to start inserting values, 0 based expected
	 *        and handled value types are: {@link String} {@link java.util.date} is formatted using
	 *        {@link java.text.dateFormat${symbol_pound}MEDIUM} optionally
	 *        displaying time when time is other than 00:00:00 {@link Number} is
	 *        formatted using {@link java.text.numberFormat} {@link Boolean} is
	 *        formatted as "X" for true, "" for false
	 * @throws ProcessingException
	 */
	protected void fillTable(Tbl table, int startRow, int startCol, Object[][] data) throws ProcessingException {
		if (table == null) {
			throw new ProcessingException("Table is not set");
		}

		try {
			List<Tr> rows = getRowsOfTable(table);
			Tr templateRow = rows.get(rows.size() - 1);
			removeMarkup(templateRow);

			// create row if necessary
			while (startRow + data.length > getRowsOfTable(table).size()) {
				Tr rowCopy = XmlUtils.deepCopy(templateRow);
				table.getContent().add(rowCopy);
			}

			for (Object[] array : data) {
				// fill cells
				int rowCol = startCol;
				Tr tr = getRowOfTable(table, startRow);
				if (tr == null) {
					return;
				}
				for (Object o : array) {
					Tc tc = getColumnOfRow(tr, rowCol);
					setTextInColumn(tc, ObjectUtility.nvl(FormattingUtility.formatObject(o), ""));
					rowCol++;
				}
				startRow++;
			}
		}
		catch (Exception e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		}
	}

	/**
	 * Returns a list of table rows.
	 *
	 * @param table
	 *          Table
	 * @return List of table rows.
	 */
	protected List<Tr> getRowsOfTable(Tbl table) {
		List<Tr> rows = new ArrayList<Tr>();
		for (Object tableContent : table.getContent()) {
			if (tableContent instanceof Tr) {
				rows.add((Tr) tableContent);
			}
		}
		return rows;
	}

	/**
	 * Returns a specific row of the table.
	 *
	 * @param table
	 *          Table
	 * @param index
	 *          Index of row
	 * @return Row referenced by index.
	 */
	protected Tr getRowOfTable(Tbl table, int index) {
		int i = 0;
		for (Object tableContent : table.getContent()) {
			if (tableContent instanceof Tr) {
				if (i == index) {
					return (Tr) tableContent;
				}
				i++;
			}
		}
		return null;
	}

	/**
	 * Returns a specific table of the row.
	 *
	 * @param row
	 *          Row
	 * @param index
	 *          Index of column
	 * @return Column referenced by index
	 */
	protected Tc getColumnOfRow(Tr row, int index) {
		int i = 0;
		for (Object rowContent : row.getContent()) {
			if (rowContent instanceof JAXBElement<?>
			&& ((JAXBElement<?>) rowContent).getValue() instanceof Tc) {
				if (i == index) {
					return (Tc) ((JAXBElement<?>) rowContent).getValue();
				}
				i++;
			}
		}
		return null;
	}

	/**
	 * Returns the jaxb element of the column.
	 *
	 * @param row
	 * @param index
	 * @return
	 */
	protected JAXBElement<?> getJaxbelementOfColumnOfRow(Tr row, int index) {
		int i = 0;
		for (Object rowContent : row.getContent()) {
			if (rowContent instanceof JAXBElement<?>
			&& ((JAXBElement<?>) rowContent).getValue() instanceof Tc) {
				if (i == index) {
					return (JAXBElement<?>) rowContent;
				}
				i++;
			}
		}
		return null;
	}

	/**
	 * Sets the text to the column.
	 *
	 * @param column
	 *          Column
	 * @param value
	 *          Text
	 */
	public void setTextInColumn(Tc column, String value) {
		if (column == null) {
			return;
		}
		value = cleanPlaceholderValue(value);
		for (Object columnP : column.getContent()) {
			if (!(columnP instanceof P)) {
				continue;
			}
			P p = (P) columnP;
			for (Object pRun : p.getContent()) {
				if (pRun instanceof R) {
					R r = (R) pRun;
					for (Object element : r.getContent()) {
						Object t = XmlUtils.unwrap(element);
						if (t instanceof Text) {
							((Text) t).setValue(value);
							return;
						}
					}
				}
			}

			// text has not been added so far
			ParaRPr pRpr = p.getPPr().getRPr();
			insertTextInParagraph(new ObjectFactory(),
					createRprFromParaRpr(pRpr), p, 0, value);
			return;
		}
	}

	/**
	 * Creates an RPr based on an ParaRpr.
	 *
	 * @param pRpr
	 * @return
	 */
	protected RPr createRprFromParaRpr(ParaRPr pRpr) {
		// unfortunately this method is needed cause the rpr object of the paragraph has another type than the rpr object from the run
		ObjectFactory factory = new ObjectFactory();
		RPr rpr = factory.createRPr();
		rpr.setB(pRpr.getB());
		rpr.setBCs(pRpr.getBCs());
		rpr.setBdr(pRpr.getBdr());
		rpr.setCaps(pRpr.getCaps());
		rpr.setColor(pRpr.getColor());
		rpr.setCs(pRpr.getCs());
		rpr.setDstrike(pRpr.getDstrike());
		rpr.setEastAsianLayout(pRpr.getEastAsianLayout());
		rpr.setEffect(pRpr.getEffect());
		rpr.setEm(pRpr.getEm());
		rpr.setEmboss(pRpr.getEmboss());
		rpr.setFitText(pRpr.getFitText());
		rpr.setHighlight(pRpr.getHighlight());
		rpr.setI(pRpr.getI());
		rpr.setICs(pRpr.getICs());
		rpr.setImprint(pRpr.getImprint());
		rpr.setKern(pRpr.getKern());
		rpr.setLang(pRpr.getLang());
		rpr.setNoProof(pRpr.getNoProof());
		rpr.setOMath(pRpr.getOMath());
		rpr.setOutline(pRpr.getOutline());
		rpr.setPosition(pRpr.getPosition());
		rpr.setRFonts(pRpr.getRFonts());
		rpr.setRStyle(pRpr.getRStyle());
		rpr.setRtl(pRpr.getRtl());
		rpr.setShadow(pRpr.getShadow());
		rpr.setShd(pRpr.getShd());
		rpr.setSmallCaps(pRpr.getSmallCaps());
		rpr.setSnapToGrid(pRpr.getSnapToGrid());
		rpr.setSpacing(pRpr.getSpacing());
		rpr.setSpecVanish(pRpr.getSpecVanish());
		rpr.setStrike(pRpr.getStrike());
		rpr.setSz(pRpr.getSz());
		rpr.setSzCs(pRpr.getSzCs());
		rpr.setU(pRpr.getU());
		rpr.setVanish(pRpr.getVanish());
		rpr.setVertAlign(pRpr.getVertAlign());
		rpr.setW(pRpr.getW());
		rpr.setWebHidden(pRpr.getWebHidden());
		return rpr;
	}

	/**
	 * Saves the document. If this adapter did not open
	 */
	public File save() throws ProcessingException {
		if (m_package == null) {
			return null;
		}
		if (m_file == null) {
			throw new ProcessingException("File is not set. Adapter was not created by opening a file. Use saveAs instead.");
		}
		try {
			m_package.save(m_file);
			return m_file;
		}
		catch (Exception e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		}
	}

	/**
	 * Saves the current document under the given filename.
	 * <p>
	 * Before saving: following preparation tasks can be activated by different flags:
	 * <ol>
	 * <li><b>unlinkFields</b>: unlink all fields in current document by considering every doc variable field in the
	 * normal text and every field inside text areas</li>
	 * </ol>
	 * </p>
	 *
	 * @param filename
	 *          the filename where the current document should be saved
	 * @param unlinkFields
	 *          flag to indicate if the fields should be unlinked before saving.
	 *          Unlinking does only make sense if the fields will be updated before
	 * @throws ProcessingException
	 */
	public File saveAs(String filename) throws ProcessingException {
		if (m_package == null) {
			return null;
		}
		try {
			File file = new File(filename);
			m_package.save(file);
			return file;
		}
		catch (Exception e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		}
	}

	/**
	 * find a table in word doc by its index num
	 *
	 * @param int: index
	 * @return Tbl
	 * @throws ProcessingException
	 */
	protected Tbl findTableByIndex(int index) throws ProcessingException {
		return findTableByIndex(new int[]{index});
	}

	/**
	 * find a table in word doc by its index num
	 *
	 * @param int[]: array of indexes
	 * @return Tbl
	 * @throws ProcessingException
	 */
	protected Tbl findTableByIndex(int[] indices) throws ProcessingException {
		if (m_package == null || indices == null || indices.length == 0) {
			return null;
		}

		final String xpath = "//w:tbl";

		try {
			List<Object> objects = getMainDocumentPart().getJAXBNodesViaXPath(xpath, false);
			if (objects == null || objects.size() == 0) {
				return null;
			}

			for (int index : indices) {
				if (objects.size() > index && objects.get(index) instanceof JAXBElement<?>) {
					Object tbl = ((JAXBElement<?>) objects.get(index)).getValue();
					if (tbl instanceof Tbl) {
						return (Tbl) tbl;
					}
				}
			}
		}
		catch (JAXBException e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		} 
		catch (XPathBinderAssociationIsPartialException e) {
			throw new ProcessingException(UNABLE_EXECUTE_OPERATION, e);
		}
		
		return null;
	}

	/**
	 * find a table in word doc by its bookmark name
	 *
	 * @param String
	 *          : name of bookmark
	 * @return Table
	 * @throws ProcessingException
	 */
	protected Tbl findTableByBookmark(String name) throws JAXBException {
		if (m_package == null) {
			return null;
		}
		
		final String xpath = "//w:bookmarkStart[@w:name='" 
		        + name
				+ "']/../../../..";
		List<Object> objects = null;
		
		try {
			objects = getMainDocumentPart().getJAXBNodesViaXPath(xpath, false);
		} 
		catch (XPathBinderAssociationIsPartialException e) {
			LOG.error("", e);
		}
		
		if (objects != null && objects.size() > 0) {
			Object tbl = XmlUtils.unwrap(objects.get(0));
			if (tbl instanceof Tbl) {
				// explicitly check for Tbl instance, because there might be
				// others too
				return (Tbl) tbl;
			}
			else {
				LOG.warn("Found possible table bookmark " + name
						+ " but it is not a table");
			}
		}
		return null;
	}

	/**
	 * Returns the main document part.
	 *
	 * @return Main document part
	 */
	protected MainDocumentPart getMainDocumentPart() {
		if (m_package == null) {
			return null;
		}
		return m_package.getMainDocumentPart();
	}

	/**
	 * Applies the given xpath expression on different document parts (i.e. headers, body, footers) and invokes the given
	 * processor for each matching document node.
	 * <p/>
	 * <b>Note:</b> The method applies all changes to JAXB elements (i.e. invokes {@link ${symbol_pound}applyChangesToJaxbElements()}).
	 *
	 * @param xpath
	 *          XPath expression for selecting docx4j document nodes.
	 * @param processor
	 *          processor applied on every matching document node.
	 * @return Returns <code>true</code> if the document structure has been
	 *         changed.
	 * @throws JAXBException
	 * @throws ProcessingException
	 */
	public boolean processDocumentParts(String xpath, IDocxNodeProcessor processor) throws ProcessingException {
		boolean changed = false;
		try {
			// headers
			for (HeaderPart headerPart : getAllHeaderParts()) {
				if (headerPart.getBinder() == null) {
					// header part is auto-created and is not based on an existing XML-element. Hence there is no binder and therefore XPath queries cannot be performed.
					continue;
				}
				for (Object node : headerPart.getJAXBNodesViaXPath(xpath, false)) {
					changed |= processor.process(node);
				}
			}

			// main document part
			for (Object node : getMainDocumentPart().getJAXBNodesViaXPath(xpath, false)) {
				changed |= processor.process(node);
			}

			// footers
			for (FooterPart footerPart : getAllFooterParts()) {
				if (footerPart.getBinder() == null) {
					// footer part is auto-created and is not based on an existing XML-element. Hence there is no binder and therefore XPath queries cannot be performed.
					continue;
				}
				for (Object node : footerPart.getJAXBNodesViaXPath(xpath, false)) {
					changed |= processor.process(node);
				}
			}
		}
		catch (JAXBException | XPathBinderAssociationIsPartialException e) {
			throw new ProcessingException("Error while processing document parts", e);
		}
		finally {
			if (changed) {
				applyChangesToJaxbElements();
			}
		}
		return changed;
	}

	/**
	 * Invokes the given processor on complex fields that are found within paragraphs selected by the given xpath
	 * expression. All headers, the document's body and all footers are searched for paragraphs.
	 * <p/>
	 * <b>Note:</b> The method applies all changes to JAXB elements (i.e. invokes {@link ${symbol_pound}applyChangesToJaxbElements()}).
	 *
	 * @param paragraphXpath
	 *          XPath expression for selecting paragraph nodes in a docx4j
	 *          document.
	 * @param fieldProcessor
	 *          processor invoked on every paragraph selected by the given xpath
	 *          expression.
	 * @return Returns <code>true</code> if the document has been changed.
	 * @throws ProcessingException
	 */
	public boolean processComplexFields(String paragraphXpath, final IDocxComplexFieldProcessor fieldProcessor) throws ProcessingException {
		return processDocumentParts(paragraphXpath, new IDocxNodeProcessor() {
			@Override
			public boolean process(Object documentNode) throws JAXBException, ProcessingException {
				boolean changed = false;
				Object unwrappedDcoumentNode = XmlUtils.unwrap(documentNode);
				if (unwrappedDcoumentNode == null) {
					throw new ProcessingException("document node must not be null");
				}
				if (!(unwrappedDcoumentNode instanceof P)) {
					throw new ProcessingException("Only paragraphs are supported for processing complex fields. But given document node has type [" + unwrappedDcoumentNode.getClass() + "]");
				}

				P p = (P) unwrappedDcoumentNode;
				boolean withinFldCharSection = false;
				boolean withinFldCharSeparate = false;

				DocxComplexField complexField = new DocxComplexField();
				for (Object content : new ArrayList<Object>(p.getContent())) {
					Object unwrappedContent = XmlUtils.unwrap(content);
					if (unwrappedContent instanceof R) {
						R run = (R) unwrappedContent;
						STFldCharType fldCharType = getFldCharType(run);
						if (fldCharType == STFldCharType.BEGIN) {
							// complex field begin
							if (withinFldCharSection) {
								throw new ProcessingException("invalid document: second fldChar start within fldChar start");
							}
							withinFldCharSection = true;
							complexField.setBeginR(run);
							continue;
						}
						else if (fldCharType == STFldCharType.SEPARATE) {
							// complex field separate
							if (!withinFldCharSection) {
								throw new ProcessingException("invalid document: fldChar separate without fldChar start");
							}
							if (withinFldCharSeparate) {
								throw new ProcessingException("invalid document: second fldChar separate within same fldChar start");
							}
							withinFldCharSeparate = true;
							complexField.setSeparateR(run);
							continue;
						}
						else if (fldCharType == STFldCharType.END) {
							// complex field end
							if (!withinFldCharSection) {
								throw new ProcessingException("invalid document: fldChar end without fldChar start");
							}
							withinFldCharSection = false;
							complexField.setEndR(run);
							changed |= fieldProcessor.processField(p, complexField);

							// restart search
							complexField = new DocxComplexField();
							withinFldCharSection = false;
							withinFldCharSeparate = false;
							continue;
						}
					}

					if (withinFldCharSeparate) {
						// child is after complex type separate (i.e. the field's display text)
						complexField.addDefaultValueContent(content);
					}
					else if (withinFldCharSection) {
						complexField.addCodeContent(content);
					}
				}

				if (withinFldCharSection) {
					throw new ProcessingException(
							"invalid document: fldChar start without fldChar end");
				}
				return changed;
			}
		});
	}

	/**
	 * @return Returns all available header parts (i.e. default, first, even header).
	 */
	protected Set<HeaderPart> getAllHeaderParts() {
		Set<HeaderPart> headers = new HashSet<HeaderPart>();
		List<SectionWrapper> sections = getPackage().getDocumentModel().getSections();
		for (SectionWrapper section : sections) {
			HeaderFooterPolicy hfp = section.getHeaderFooterPolicy();
			if (hfp != null) {
				if (hfp.getDefaultHeader() != null) {
					headers.add(hfp.getDefaultHeader());
				}
				if (hfp.getFirstHeader() != null) {
					headers.add(hfp.getFirstHeader());
				}
				if (hfp.getEvenHeader() != null) {
					headers.add(hfp.getEvenHeader());
				}
			}
		}
		return headers;
	}

	/**
	 * @return Returns all available footer parts (i.e. default, first, even header).
	 */
	protected Set<FooterPart> getAllFooterParts() {
		Set<FooterPart> footers = new HashSet<FooterPart>();
		for (SectionWrapper section : getPackage().getDocumentModel().getSections()) {
			HeaderFooterPolicy hfp = section.getHeaderFooterPolicy();
			if (hfp != null) {
				if (hfp.getDefaultFooter() != null) {
					footers.add(hfp.getDefaultFooter());
				}
				if (hfp.getFirstFooter() != null) {
					footers.add(hfp.getFirstFooter());
				}
				if (hfp.getEvenFooter() != null) {
					footers.add(hfp.getEvenFooter());
				}
			}
		}
		return footers;
	}

	/**
	 * Ensures the there is a settings.xml file available with the tag CTCompat to prevent word from using
	 * "compatibility mode".
	 */
	protected final void ensureCompatibilitySettings() {
		try {
			ObjectFactory factory = new ObjectFactory();
			DocumentSettingsPart dsp = getMainDocumentPart().getDocumentSettingsPart();
			if (dsp == null) {
				dsp = new DocumentSettingsPart();
				getMainDocumentPart().addTargetPart(dsp);
			}

			CTSettings settings = dsp.getContents();
			if (settings == null) {
				settings = factory.createCTSettings();
				dsp.setJaxbElement(settings);
			}

			CTCompat compat = settings.getCompat();
			if (compat == null) {
				compat = new CTCompat();
				settings.setCompat(compat);
			}

			for (CTCompatSetting cs : compat.getCompatSetting()) {
				if (WORD_SCHEMA_NAMESPACE.equals(cs.getUri()) && COMPATIBILITY_MODE_NAME.equals(cs.getName())) {
					// there are already compatibility settings available
					return;
				}
			}

			// compatibility settings are not available
			CTCompatSetting compatSetting = new CTCompatSetting();
			compatSetting.setName(COMPATIBILITY_MODE_NAME);
			compatSetting.setUri(WORD_SCHEMA_NAMESPACE);
			compatSetting.setVal(WORD_2010_INTERNAL_VERSION);

			compat.getCompatSetting().add(compatSetting);

		}
		catch (Docx4JException e) {
			LOG.info("error adding compatibility settings", e);
		}
	}

	/**
	 * Convenience method for {@link ${symbol_pound}applyChangesToJaxbElements(WordprocessingMLPackage)}.
	 */
	protected void applyChangesToJaxbElements() {
		try {
			m_package = applyChangesToJaxbElements(m_package);
		}
		catch (Exception e) {
			LOG.warn("Exception while applying changes to JAXB elements", e);
		}
	}

	/**
	 * Removes recursively any markup elements form the given docx4j document element.
	 */
	protected void removeMarkup(Object o) {
		if (o == null) {
			return;
		}

		Object unwrapped = XmlUtils.unwrap(o);
		if (unwrapped instanceof ContentAccessor) {
			ContentAccessor ca = (ContentAccessor) unwrapped;
			if (ca.getContent() == null) {
				return;
			}

			for (Iterator<Object> it = ca.getContent().iterator(); it.hasNext();) {
				Object next = XmlUtils.unwrap(it.next());
				// filter markup elements
				if (next instanceof CTMarkup // bookmarks, comments, change
						// control
						|| next instanceof ProofErr) { // spelling checker marks
					it.remove();
				}
				else {
					removeMarkup(next);
				}
			}
		}
	}

	/**
	 * Workaround for a poorly initialized or outdated JAXB context. The word processing ML package is serialized into its
	 * XML and parsed again.
	 * <p/>
	 * docx4j documentation about modifying documents using XPath: <blockquote> There is a limitation however: the xpath
	 * expressions are evaluated against the XML document as it was when first opened in docx4j. You can update the
	 * associated XML document once only, by passing true into getJAXBNodesViaXPath. Updating it again (with current JAXB
	 * 2.1.x or 2.2.x) will cause an error. </blockquote>
	 */
	public static WordprocessingMLPackage applyChangesToJaxbElements(WordprocessingMLPackage pkg) throws Docx4JException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Save saver = new Save(pkg);
		saver.save(out);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return WordprocessingMLPackage.load(in);
	}
}
