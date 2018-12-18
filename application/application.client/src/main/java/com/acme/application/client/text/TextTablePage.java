package com.acme.application.client.text;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.acme.application.client.common.AbstractExportableTable;
import com.acme.application.client.text.TextTablePage.Table;
import com.acme.application.shared.FontAwesomeIcons;
import com.acme.application.shared.code.LocaleCodeType;
import com.acme.application.shared.text.ITextService;
import com.acme.application.shared.text.ReadTextPagePermission;
import com.acme.application.shared.text.TextTablePageData;

@Data(TextTablePageData.class)
public class TextTablePage extends AbstractPageWithTable<Table> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("TextTablePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected void execInitPage() {
		setVisiblePermission(new ReadTextPagePermission());
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(ITextService.class).getTextTableData(filter));
	}	
	
	public class Table extends AbstractExportableTable {
		
		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}
		
		@Order(1000)
		public class TranslateMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Translate");
			}

			@Override
			protected String getConfiguredIconId() {
				return FontAwesomeIcons.fa_language;
			}

			@Override
			protected String getConfiguredKeyStroke() {
				return "alt-t";
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.SingleSelection);
			}

			@Override
			protected void execAction() {
				String textId = getKeyColumn().getSelectedValue();

				TextForm form = new TextForm();
				form.setKey(textId);
				form.startModify();
				form.waitFor();

				if (form.isFormStored()) {
					reloadPage();
				}
			}
		}

		public LocaleColumn getLocaleColumn() {
			return getColumnSet().getColumnByClass(LocaleColumn.class);
		}

		public TextColumn getTextColumn() {
			return getColumnSet().getColumnByClass(TextColumn.class);
		}

		public KeyColumn getKeyColumn() {
			return getColumnSet().getColumnByClass(KeyColumn.class);
		}

		@Override
		protected void execRowAction(ITableRow row) {
			getMenuByClass(TranslateMenu.class).execAction();
		}
		
		@Order(1000)
		public class KeyColumn extends AbstractStringColumn {

			@Override
			protected boolean getConfiguredPrimaryKey() {
				return true;
			}

			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Key");
			}

			@Override
			protected int getConfiguredWidth() {
				return 250;
			}
		}

		@Order(2000)
		public class LocaleColumn extends AbstractSmartColumn<String> {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Locale");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
			
			@Override
			protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
				return LocaleCodeType.class;
			}
		}

		@Order(3000)
		public class TextColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("TranslatedText");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}
	}
}
