#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.shared.security;

// Code origin: org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Password utility based on BCrypt algorithm.
 * Sources copied from Spring repo
 * https://github.com/spring-projects/spring-security/blob/master/crypto/src/main/java/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.java
 * https://github.com/spring-projects/spring-security/blob/master/crypto/src/main/java/org/springframework/security/crypto/bcrypt/BCrypt.java
 * Spring sources have most likely been copied from http://www.mindrot.org/projects/jBCrypt/
 */
public class PasswordUtility {

	public static final int PASSWORD_LENGTH_MIN = 4;
	public static final int PASSWORD_LENGTH_MAX = 64;

	private static Pattern BCRYPT_PATTERN = Pattern
			.compile("${symbol_escape}${symbol_escape}A${symbol_escape}${symbol_escape}${symbol_dollar}2a?${symbol_escape}${symbol_escape}${symbol_dollar}${symbol_escape}${symbol_escape}d${symbol_escape}${symbol_escape}d${symbol_escape}${symbol_escape}${symbol_dollar}[./0-9A-Za-z]{53}");

	private static final Logger LOG = LoggerFactory.getLogger(PasswordUtility.class);

	public static boolean matchesPasswordPolicy(String password) {
		if (!StringUtility.hasText(password)) {
			LOG.error("Empty plain password provided");
			return false;
		}

		if (password.length() < PASSWORD_LENGTH_MIN || password.length() > PASSWORD_LENGTH_MAX) {
			LOG.error("Provided password is too short or too long");
			return false;
		}

		return true;
	}

	/**
	 * Provides a password hash for the provided plain text password. 
	 * Hashing is done using BCrypt 
	 */
	public static String calculateEncodedPassword(String passwordPlain) {
		return BCrypt.hashpw(passwordPlain, BCrypt.gensalt());
	}

	public static boolean passwordIsValid(String passwordPlain, String passwordEncoded) {
		if (!StringUtility.hasText(passwordPlain)) {
			LOG.error("Empty plain password");
			return false;
		}

		if (!StringUtility.hasText(passwordEncoded)) {
			LOG.error("Empty encoded password");
			return false;
		}

		if (!BCRYPT_PATTERN.matcher(passwordEncoded).matches()) {
			LOG.warn("Encoded password does not look like BCrypt");
			return false;
		}

		return BCrypt.checkpw(passwordPlain.toString(), passwordEncoded);
	}
}
