#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.document;

import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBigIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateTimeColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.dnd.IDNDSupport;
import org.eclipse.scout.rt.client.ui.dnd.TransferObject;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import ${package}.client.common.AbstractActiveColumn;
import ${package}.client.common.AbstractDeleteMenu;
import ${package}.client.common.AbstractDownloadMenu;
import ${package}.client.common.AbstractExportableTable;
import ${package}.client.common.AbstractIdColumn;
import ${package}.client.common.DownloadUtility;
import ${package}.client.user.UserLookupCall;
import ${package}.shared.code.FileCodeType;
import ${package}.shared.document.IDocumentService;
import ${package}.shared.payment.PaymentFormData.DocumentTable;

public abstract class AbstractDocumentTable extends AbstractExportableTable {

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

	@Order(1000)
	@ClassId("83aa2710-5784-47b1-b903-d818c2d17f87")
	public class IdColumn extends AbstractIdColumn {
		@Override
		protected String getConfiguredHeaderText() {
			return TEXTS.get("ID");
		}
	}

	@Order(2000)
	@ClassId("be7a023e-c2e2-4c6f-bd92-864d3e14d22f")
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
	@ClassId("6572a221-ccd2-44da-93c7-1326047c0f05")
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
	@ClassId("55345daf-3403-4639-9314-0db88fef0c49")
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
	@ClassId("2e5a93c8-cadd-49ba-bdeb-cd251dbfb493")
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
	@ClassId("4f2aaa6e-8113-4e6f-ae04-f06a884fc4a1")
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
	@ClassId("21353add-87ad-4f92-a256-c714f7542d67")
	public class ActiveColumn extends AbstractActiveColumn {
	}

	@Override
    protected void execDrop(ITableRow row, TransferObject t) {
		String paymentId = getPaymentId();
    	DownloadUtility.upload(t, paymentId);
    	importFieldData(BEANS.get(IDocumentService.class).getDocumentTableData(paymentId));
    	reloadTableData();
    }

	@Override
	protected int getConfiguredDropType() {
		return IDNDSupport.TYPE_FILE_TRANSFER;
	}

	@Override
	protected long getConfiguredDropMaximumSize() {
		return IDNDSupport.DEFAULT_DROP_MAXIMUM_SIZE;
	}

	@Order(1000)
	@ClassId("d011275b-9983-4a34-8f18-69528379d4a2")
	public class DownloadMenu extends AbstractDownloadMenu {
		@Override
		protected void execAction() {
			int row = getSelectedRow().getRowIndex();
			String docId = getIdColumn().getValue(row);
			BinaryResource doc = BEANS.get(IDocumentService.class).getResource(docId);
			DownloadUtility.download(doc);
		}
	}

	@Order(2000)
	@ClassId("aaa5afba-c507-44be-a45d-f62b713f5771")
	public class DeleteMenu extends AbstractDeleteMenu {
		@Override
		protected void execAction() {
			IDocumentService service = BEANS.get(IDocumentService.class);
			getIdColumn().getSelectedValues()
				.stream()
				.forEach(service::delete);
			importFieldData(BEANS.get(IDocumentService.class).getDocumentTableData(getPaymentId()));
	    	reloadTableData();
		}

		@Override
		public AbstractSmartColumn<String> getOwner() {
			return getUserColumn();
		}
	}

	protected abstract void importFieldData(DocumentTable documentTable);

	protected abstract void reloadTableData();

	protected abstract String getPaymentId();

}
