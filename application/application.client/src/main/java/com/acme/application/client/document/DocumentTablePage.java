package com.acme.application.client.document;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBigIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateTimeColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.dnd.IDNDSupport;
import org.eclipse.scout.rt.client.ui.dnd.ResourceListTransferObject;
import org.eclipse.scout.rt.client.ui.dnd.TransferObject;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.acme.application.client.ClientSession;
import com.acme.application.client.common.AbstractExportableTable;
import com.acme.application.client.common.DownloadUtility;
import com.acme.application.client.document.DocumentTablePage.Table;
import com.acme.application.client.user.UserLookupCall;
import com.acme.application.shared.FontAwesomeIcons;
import com.acme.application.shared.code.FileCodeType;
import com.acme.application.shared.document.DocumentTablePageData;
import com.acme.application.shared.document.IDocumentService;

@Data(DocumentTablePageData.class)
public class DocumentTablePage extends AbstractPageWithTable<Table> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("DocumentTablePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(IDocumentService.class).getDocumentTableData(filter));
	}

	public class Table extends AbstractExportableTable  {

		@Override
		public IOutline getPageOutline() {
			return getOutline();
		}

		public SizeColumn getSizeColumn() {
			return getColumnSet().getColumnByClass(SizeColumn.class);
		}

		public UserColumn getUserColumn() {
			return getColumnSet().getColumnByClass(UserColumn.class);
		}

		public ActiveColumn getActiveColumn() {
			return getColumnSet().getColumnByClass(ActiveColumn.class);
		}

		public UploadedColumn getUploadedColumn() {
			return getColumnSet().getColumnByClass(UploadedColumn.class);
		}

		public TypeColumn getTypeColumn() {
			return getColumnSet().getColumnByClass(TypeColumn.class);
		}

		public NameColumn getNameColumn() {
			return getColumnSet().getColumnByClass(NameColumn.class);
		}

		public IdColumn getIdColumn() {
			return getColumnSet().getColumnByClass(IdColumn.class);
		}

		@Override
		protected int getConfiguredDropType() {
			return IDNDSupport.TYPE_FILE_TRANSFER;
		}
		
		@Override
		protected long getConfiguredDropMaximumSize() {
			return IDNDSupport.DEFAULT_DROP_MAXIMUM_SIZE;
		}
		
	    @Override
	    protected void execDrop(ITableRow row, TransferObject t) {
	      try {
	        if (t instanceof ResourceListTransferObject) {
	          for (BinaryResource resource : ((ResourceListTransferObject) t).getResources()) {
	            addFile(resource);
	          }
	        }
	        
	        reloadPage();
	      }
	      catch (RuntimeException e) {
	        throw new ProcessingException(e.getMessage(), e);
	      }
	    }

	    private void addFile(BinaryResource file) {
	    	String name = file.getFilename();
	    	String username = ClientSession.get().getUserId();
	    	byte [] content = file.getContent();
	    	
	    	BEANS.get(IDocumentService.class).store(name, content, username);
	    }
		
		@Order(100000)
		public class DownloadMenu extends AbstractMenu {

			@Override
			protected String getConfiguredIconId() {
				return FontAwesomeIcons.fa_download;
			}

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Download");
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.SingleSelection);
			}

			@Override
			protected void execAction() {
				int row = getTable().getSelectedRow().getRowIndex();
				String docId = getTable().getIdColumn().getValue(row);
				BinaryResource doc = BEANS.get(IDocumentService.class).getResource(docId);
				DownloadUtility.download(doc);
			}
		}

		@Order(1000)
		public class IdColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("ID");
			}

			@Override
			protected boolean getConfiguredPrimaryKey() {
				return true;
			}

			@Override
			protected boolean getConfiguredDisplayable() {
				return false;
			}
		}

		@Order(2000)
		public class NameColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Name");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(2500)
		public class TypeColumn extends AbstractSmartColumn<String> {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Type");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
			
			@Override
			protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
				return FileCodeType.class;
			}
		}

		@Order(3000)
		public class SizeColumn extends AbstractBigIntegerColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Size");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(4000)
		public class UserColumn extends AbstractSmartColumn<String> {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("User");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}

			@Override
			protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
				return UserLookupCall.class;
			}
		}

		@Order(5000)
		public class UploadedColumn extends AbstractDateTimeColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Uploaded");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(6000)
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
