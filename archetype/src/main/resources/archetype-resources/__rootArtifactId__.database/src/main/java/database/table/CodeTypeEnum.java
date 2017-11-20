#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.database.table;

public enum CodeTypeEnum {
	STRING("java.lang.String"),
	LOCALE("java.lang.Locale");
	
	private String type;
	
	CodeTypeEnum(String type) {
		this.type = type;
	}
	
	public String type() {
		return type;
	}
}
