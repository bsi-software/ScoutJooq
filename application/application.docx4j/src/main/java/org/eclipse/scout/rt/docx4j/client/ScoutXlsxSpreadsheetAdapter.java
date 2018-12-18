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
package org.eclipse.scout.rt.docx4j.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.eclipse.scout.docx4j.XlsxAdapter;
import org.eclipse.scout.rt.client.services.common.file.FileService;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPageWithTable;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.ScoutTexts;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.date.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractService;

/**
 * Scout xlsx spreadsheet adapter using {@link XlsxAdapter).
 *
 * @since 1.0.0
 */
public class ScoutXlsxSpreadsheetAdapter extends AbstractService {

	private static final Logger LOG = LoggerFactory.getLogger(ScoutXlsxSpreadsheetAdapter.class);

	static final String EXPORTING_LOG_MESSAGE = "exporting ";


	@Override
	protected void doStart() {
	}

	@Override
	protected void doStop() {
	}
	
	public File exportPage(String templateName, int startRow, int startCol, IPage<?> page) throws ProcessingException {
		return exportPage(templateName, null, startRow, startCol, page, false);
	}

	public File exportPage(String templateName, Locale locale, int startRow, int startCol, IPage<?> page) throws ProcessingException {
		return exportPage(templateName, locale, startRow, startCol, page, false);
	}

	public File exportPage(String templateName, int startRow, int startCol, IPage<?> page, boolean infoRowsAtBegin) throws ProcessingException {
		return exportPage(templateName, null, startRow, startCol, page, infoRowsAtBegin, false);
	}

	public File exportPage(String templateName, Locale locale, int startRow, int startCol, IPage<?> page, boolean infoRowsAtBegin) throws ProcessingException {
		return exportPage(templateName, locale, startRow, startCol, page, infoRowsAtBegin, false);
	}

	public File exportPage(String templateName, int startRow, int startCol, IPage<?> page, boolean infoRowsAtBegin, boolean writeDateAtEnd) throws ProcessingException {
		if (page instanceof IPageWithTable<?>) {
			return exportTablePage(templateName, null, startRow, startCol, (IPageWithTable<?>) page, infoRowsAtBegin, writeDateAtEnd);
		}
		else {
			return exportNodePage(templateName, null, startRow, startCol, (IPageWithNodes) page, infoRowsAtBegin, writeDateAtEnd);
		}
	}

	public File exportPage(String templateName, Locale locale, int startRow, int startCol, IPage<?> page, boolean infoRowsAtBegin, boolean writeDateAtEnd) throws ProcessingException {
		if (page instanceof IPageWithTable<?>) {
			return exportTablePage(templateName, locale, startRow, startCol, (IPageWithTable<?>) page, infoRowsAtBegin, writeDateAtEnd);
		}
		else {
			return exportNodePage(templateName, locale, startRow, startCol, (IPageWithNodes) page, infoRowsAtBegin, writeDateAtEnd);
		}
	}

	public File exportNodePage(String templateName, int startRow, int startCol, IPageWithNodes page, boolean infoRowsAtBegin, boolean writeDateAtEnd) throws ProcessingException {
		return exportNodePage(templateName, null, startRow, startCol, page, infoRowsAtBegin, writeDateAtEnd);
	}

	public File exportNodePage(String templateName, Locale locale, int startRow, int startCol, IPageWithNodes page, boolean infoRowsAtBegin, boolean writeDateAtEnd) throws ProcessingException {
		if (page == null) {
			throw new ProcessingException("No page found to export.");
		}
		String title = page.getCell().getText();
		Object[][] csv;

		csv = new Object[page.getChildNodeCount() + 1][1];
		csv[0][0] = "";
		for (int i = 0; i < page.getChildNodeCount(); i++) {

			String text = page.getChildNodes().get(i).getCell().getText();
			csv[i + 1][0] = text;
		}

		// build verbose search text
		// info rows
		ArrayList<String> infoRowList = new ArrayList<String>();
		infoRowList.add(TEXTS.get("Path") + ": " + page.getTree().getPathText(page));
		infoRowList.add(TEXTS.get("NumberOfRows") + ": " + (csv.length - 1));
		if (writeDateAtEnd) {
			infoRowList.add(TEXTS.get("ExportDate") + ": " + DateUtility.formatDate(new Date()));
		}
		String[] infoRows = infoRowList.toArray(new String[0]);
		//
		return exportCsv(templateName, locale, startRow, startCol, title, csv, infoRowsAtBegin, infoRows);
	}

	public File exportTablePage(String templateName, int startRow, int startCol, IPageWithTable<?> page, boolean infoRowsAtBegin, boolean writeDateAtEnd) throws ProcessingException {
		return exportTablePage(templateName, null, startRow, startCol, page, infoRowsAtBegin, writeDateAtEnd);
	}

	public File exportTablePage(String templateName, Locale locale, int startRow, int startCol, IPageWithTable<?> page, boolean infoRowsAtBegin, boolean writeDateAtEnd) throws ProcessingException {
		String title = page.getCell().getText();
		ITable table = page.getTable();
		Object[][] csv = exportTableRowsAsCSV(table);
		// build verbose search text
		// info rows
		ArrayList<String> infoRowList = new ArrayList<String>();
		infoRowList.add(TEXTS.get("Path") + ": " + page.getTree().getPathText(page));
		if (page.getSearchFilter() != null) {
			String[] searchTree = page.getSearchFilter().getDisplayTexts();
			if (searchTree != null && searchTree.length > 0) {
				infoRowList.add(TEXTS.get("SearchConstraints"));
				for (String s : searchTree) {
					infoRowList.add("  " + s);
				}
			}
		}

		// TODO check how to reenable this
		//    if (table.getColumnFilterManager() != null) {
		//      List<String> filterTexts = table.getColumnFilterManager().getDisplayTexts();
		//      if (filterTexts != null && filterTexts.size() > 0) {
		//        infoRowList.add(ScoutTexts.get("ColumnFilterConstraints"));
		//        for (String s : filterTexts) {
		//          infoRowList.add("  " + s);
		//        }
		//      }
		//    }

		infoRowList.add(TEXTS.get("NumberOfRows") + ": " + (csv.length - 1));
		if (writeDateAtEnd) {
			infoRowList.add(TEXTS.get("ExportDate") + ": " + DateUtility.formatDate(new Date()));
		}
		String[] infoRows = infoRowList.toArray(new String[0]);
		//
		return exportCsv(templateName, locale, startRow, startCol, title, csv, infoRowsAtBegin, infoRows);
	}

	public File exportTable(String templateName, String title, ITable table) throws ProcessingException {
		return exportTable(templateName, null, 0, 0, title, table);
	}

	public File exportTable(String templateName, Locale locale, int startRow, int startCol, String title, ITable table) throws ProcessingException {
		Object[][] csv = exportTableRowsAsCSV(table);
		// build verbose search text
		// info rows
		ArrayList<String> infoRowList = new ArrayList<String>();
		infoRowList.add(TEXTS.get("NumberOfRows") + ": " + (csv.length - 1));
		infoRowList.add(TEXTS.get("ExportDate") + ": " + DateUtility.formatDate(new Date()));
		String[] infoRows = infoRowList.toArray(new String[0]);
		//
		return exportCsv(templateName, locale, startRow, startCol, title, csv, false, infoRows);
	}

	/**
	 * Exports the csv.
	 * 
	 * @param templateName
	 * @param locale
	 * @param startRow
	 * @param startCol
	 * @param title
	 * @param csv
	 * @param infoRowsAtBegin
	 *          {@code true} if the info rows should be at the begin, {@code false} if the should be at the end
	 * @param infoRows
	 *          Infos rows
	 * @return Created file
	 */
	public File exportCsv(String templateName, Locale locale, int startRow, int startCol, String title, Object[][] csv, boolean infoRowsAtBegin, String[] infoRows) {
		XlsxAdapter workbook = null;
		try {
			// init
			workbook = prepareWorkbook(templateName, locale);
			// fill
			workbook.fillCSV(startRow, startCol, title, infoRowsAtBegin ? infoRows : null, infoRowsAtBegin ? null : infoRows, csv, true);

			// show info
			// TODO check how to reenable this
			//      IDesktop desktop = ClientSyncJob.getCurrentSession().getDesktop();
			//      if (desktop != null) {
			//        desktop.setStatusText(ScoutTexts.get("StatusExportDone"));
			//      }
		}
		catch (Exception e) {
			LOG.error(EXPORTING_LOG_MESSAGE + title, e);
		}
		finally {
			if (workbook != null) {
				try {
					File tempFile = new File(IOUtility.getTempFileName(".xlsx"));
					workbook.save(tempFile);
					return tempFile;
				}
				catch (Exception e) {
					LOG.error(EXPORTING_LOG_MESSAGE + title, e);
				}
			}
		}
		return null;
	}

	protected XlsxAdapter prepareWorkbook(String templateName, Locale locale) throws ProcessingException {
		if (StringUtility.hasText(templateName)) {
			// load the template resource
			templateName = templateName.replace('\\', '/');
			String folder = null;
			if (templateName.lastIndexOf('/') != -1) {
				folder = templateName.substring(0, templateName.lastIndexOf('/'));
				templateName = templateName.substring(templateName.lastIndexOf('/') + 1);
			}
			FileService fs = new FileService();
			File f = fs.getRemoteFile(folder, templateName, locale);

			if (f == null) {
				LOG.error("Could not load template: " + templateName);
			}
			return new XlsxAdapter(f);
		}
		return new XlsxAdapter();
	}

	/**
	 * Exports the table data to a two dimensional object array.
	 * 
	 * @param table
	 *          Table to export
	 * @return Exported table
	 */
	public Object[][] exportTableRowsAsCSV(ITable table) {
		Object[][] csv;
		if (table.getSelectedRowCount() >= 2) {
			csv = table.exportTableRowsAsCSV(table.getSelectedRows(), table.getColumnSet().getVisibleColumns(), true, false, false);
		}
		else {
			csv = table.exportTableRowsAsCSV(table.getFilteredRows(), table.getColumnSet().getVisibleColumns(), true, false, false);
		}
		return csv;
	}
}
