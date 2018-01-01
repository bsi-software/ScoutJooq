#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.code;

import java.util.List;
import java.util.UUID;

import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationCodeUtility {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationCodeUtility.class);
	private static final String CODES_PACKAGE_PREFIX = "${package}";

	/**
	 * Generates and returns a new code id.
	 * Implementation based on {@link UUID${symbol_pound}randomUUID()}.
	 */
	public static String generateCodeId() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * Returns true iff the code type specified by the provided class exists.
	 */
	public static boolean exists(Class<? extends IApplicationCodeType> clazz) {
		return getCodeType(clazz) != null;
	}

	/**
	 * Returns true iff the code specified by the provided id and code type class exists.
	 */	
	public static boolean exists(Class<? extends IApplicationCodeType> clazz, String codeId) {
		IApplicationCodeType codeType = getCodeType(clazz);
		return codeType.getCode(codeId) != null ? true : false;
	}

	/**
	 * Returns all codes for the provided code type class.
	 */
	@SuppressWarnings("unchecked")
	public static List<ICode<String>> getCodes(Class<? extends IApplicationCodeType> clazz) {
		IApplicationCodeType codeType = getCodeType(clazz);
		return codeType != null ? (List<ICode<String>>) codeType.getCodes() : null;
	}

	/**
	 * Returns the code for the provided code type class and code id.
	 */
	public static ICode<String> getCode(Class<? extends IApplicationCodeType> clazz, String codeId) {
		IApplicationCodeType codeType = getCodeType(clazz);
		return getCode(codeType, codeId);
	}

	/**
	 * Returns the code for the provided code type id and code id.
	 */
	public static ICode<String> getCode(String codeTypeId, String codeId) {
		IApplicationCodeType codeType = getCodeType(codeTypeId);
		return getCode(codeType, codeId);
	}
	
	private static ICode<String> getCode(IApplicationCodeType codeType, String codeId) {
		return codeType != null ? codeType.getCode(codeId) : null;
	}

	/**
	 * Returns the code type object for the specified code type id.
	 */
	public static IApplicationCodeType getCodeType(String codeTypeId) {
		if(codeTypeId != null) {
			ICodeType<String, ?> codeType = CODES.findCodeTypeById(codeTypeId);
			
			if(codeType == null) {
				LOG.warn("No type could be found for type id '{}'", codeTypeId);
			}
			else if(!(codeType instanceof IApplicationCodeType)) {
				LOG.warn("Provided type id '{}' not of type IApplicationCodeType", codeTypeId);
			}
			
			return (IApplicationCodeType) codeType;
		}
		else {
			LOG.error("Provided null instead of a type id");
			return null;
		}
	}

	/**
	 * Returns the code type object for the specified code type class.
	 */
	public static IApplicationCodeType getCodeType(Class<? extends IApplicationCodeType> clazz) {
		if(clazz != null) {
			return (IApplicationCodeType) CODES.getCodeTypes(clazz).get(0);
		}
		else {
			LOG.error("Provided null instead of a code type class");
			return null;
		}
	}
	
	/**
	 * Reloads the code type and triggers a code cash refresh.
	 */
	public static void reload(String codeTypeId) {
		IApplicationCodeType type = getCodeType(codeTypeId);
		
		if(type != null) {
			reload(type.getClass());
		}
	}

	/**
	 * Reloads the code type and triggers a code cash refresh.
	 */
	public static void reload(Class<? extends IApplicationCodeType> clazz) {
		CODES.reloadCodeType(clazz);
	}
	
	public static void reloadAll() {
		CODES.getAllCodeTypes(CODES_PACKAGE_PREFIX);
	}
}
