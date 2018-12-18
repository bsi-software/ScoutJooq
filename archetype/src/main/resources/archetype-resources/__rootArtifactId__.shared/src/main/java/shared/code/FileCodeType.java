#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;

import ${package}.shared.FontAwesomeIcons;

public class FileCodeType extends BaseCodeType {

	private static final long serialVersionUID = 1L;

	/**
	 * ID taken from class TableDataInitializer. 
	 * IMPORTANT: This ID links the code type to the database content
	 */
	public static final String ID = "f0f3add3-83e6-4246-ad6f-2872fffbf639";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public Class<? extends IApplicationCodeType> getCodeTypeClass() {
		return FileCodeType.class;
	}

	@Order(10)
	public static class HtmlCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "html";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("HTMLDocument");
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(20)
	public static class TextCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "txt";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("TextDocument");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_fileText;
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(30)
	public static class JpgCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "jpg";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("JpgImage");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_filePictureO;
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(40)
	public static class PngCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "png";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("PngImage");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_filePictureO;
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(50)
	public static class JavaCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;
		public static final String ID = "java";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("JavaFile");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_fileCodeO;
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(60)
	public static class PdfCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;

		public static final String ID = "pdf";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("PdfDocument");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_filePdfO;
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	@Order(70)
	public static class UknownCode extends AbstractCode<String> {

		private static final long serialVersionUID = 1L;

		public static final String ID = "__UNKNOWN__";

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Unknown");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return FontAwesomeIcons.fa_fileO;
		}

		@Override
		public String getId() {
			return ID;
		}
	}
}
