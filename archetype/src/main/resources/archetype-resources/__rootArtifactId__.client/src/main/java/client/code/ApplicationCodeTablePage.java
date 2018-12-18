#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.code;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBigDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.client.ClientSession;
import ${package}.client.code.ApplicationCodeForm.DisplayMode;
import ${package}.client.code.ApplicationCodeTablePage.Table;
import ${package}.client.common.AbstractExportableTable;
import ${package}.client.text.TextForm;
import ${package}.shared.FontAwesomeIcons;
import ${package}.shared.code.ApplicationCodePageData;
import ${package}.shared.code.ApplicationCodeUtility;
import ${package}.shared.code.CreateApplicationCodePermission;
import ${package}.shared.code.IApplicationCodeService;
import ${package}.shared.code.IApplicationCodeType;
import ${package}.shared.code.UpdateApplicationCodePermission;
import ${package}.shared.text.ITextService;

@Data(ApplicationCodePageData.class)
public class ApplicationCodeTablePage extends AbstractPageWithTable<Table> {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationCodeTablePage.class);

	private IApplicationCodeType codeType = null;

	public ApplicationCodeTablePage(Class<? extends IApplicationCodeType> codeTypeClass) {
		codeType = ApplicationCodeUtility.getCodeType(codeTypeClass);
	}

	@Override
	protected String getConfiguredTitle() {
		String key = codeType.getId();
		String locale = ClientSession.get().getLocale().toLanguageTag();
		return BEANS.get(ITextService.class).getText(key, locale);
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		ApplicationCodePageData pageData = BEANS.get(IApplicationCodeService.class)
				.getApplicationCodeTableData(codeType.getCodeTypeClass());
		
		importPageData(pageData);
	}

	public class Table extends AbstractExportableTable {

		@Override
		protected void execRowAction(ITableRow row) {
			getMenuByClass(EditMenu.class).execAction();
		}

		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		@Order(10)
		public class NewMenu extends AbstractMenu {

			@Override
			protected void execInitAction() {
				setVisiblePermission(new CreateApplicationCodePermission());
			}

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("New");
			}

			@Override
			protected String getConfiguredIconId() {
				return FontAwesomeIcons.fa_magic;
			}

			@Override
			protected String getConfiguredKeyStroke() {
				return "alt-n";
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection, TableMenuType.MultiSelection);
			}

			@Override
			protected void execAction() {
				ApplicationCodeForm form = new ApplicationCodeForm();
				form.setDisplayMode(DisplayMode.CREATE);
				form.setCodeTypeId(codeType.getId());
				form.addFormListener(new ApplicationCodeFormListener());
				form.startNew();
			}
		}

		@Order(20)
		public class EditMenu extends AbstractMenu {

			@Override
			protected void execInitAction() {
				setVisiblePermission(new UpdateApplicationCodePermission());
			}

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Edit");
			}

			@Override
			protected String getConfiguredIconId() {
				return FontAwesomeIcons.fa_pencil;
			}

			@Override
			protected String getConfiguredKeyStroke() {
				return "alt-e";
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.SingleSelection);
			}

			@Override
			protected void execAction() {
				ApplicationCodeForm form = new ApplicationCodeForm();
				form.setDisplayMode(DisplayMode.EDIT);
				form.setCodeTypeId(codeType.getId());
				form.getCodeIdField().setValue(getIdColumn().getSelectedValue());
				form.addFormListener(new ApplicationCodeFormListener());
				form.startModify();
			}
		}

		@Order(30)
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
				String codeId = getIdColumn().getSelectedValue();

				TextForm form = new TextForm();
				form.setKey(codeId);
				form.startModify();
				form.waitFor();

				if (form.isFormStored()) {
					reloadPage();
				}
			}
		}

		private class ApplicationCodeFormListener implements FormListener {

			@Override
			public void formChanged(FormEvent e) {
				if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
					ApplicationCodeUtility.reload(codeType.getClass());
					reloadPage();
				}
			}
		}

		public ActiveColumn getActiveColumn() {
			getOutline();
			return getColumnSet().getColumnByClass(ActiveColumn.class);
		}

		public OrderColumn getOrderColumn() {
			return getColumnSet().getColumnByClass(OrderColumn.class);
		}

		public TextColumn getTextColumn() {
			return getColumnSet().getColumnByClass(TextColumn.class);
		}

		public TypeColumn getTypeColumn() {
			return getColumnSet().getColumnByClass(TypeColumn.class);
		}

		public IdColumn getIdColumn() {
			return getColumnSet().getColumnByClass(IdColumn.class);
		}

		@Order(1000)
		public class IdColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Id");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(2000)
		public class TypeColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("CodeType");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(3000)
		public class TextColumn extends AbstractStringColumn {

			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Text");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}

			@Override
			protected boolean getConfiguredSortAscending() {
				return true;
			}
		}

		@Order(4000)
		public class OrderColumn extends AbstractBigDecimalColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Order");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(5000)
		public class ActiveColumn extends AbstractBooleanColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Active");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}
	}
}
