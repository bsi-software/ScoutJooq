#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;

public class SexCodeType extends BaseCodeType {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * IMPORTANT: This ID links the code type to the database content
	 */
	public static final String ID = "17f42353-e6e6-4654-a879-02535cc9c44f";

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
		return SexCodeType.class;
	}
	
	@Order(10.0)
	public static class Male extends AbstractCode<String> {
		private static final long serialVersionUID = 1L;

		public static final String ID = "M";

		@Override
		public String getId() {
			return ID;
		}
	}	

	@Order(20.0)
	public static class Female extends AbstractCode<String> {
		private static final long serialVersionUID = 1L;

		public static final String ID = "F";

		@Override
		public String getId() {
			return ID;
		}
	}
}
