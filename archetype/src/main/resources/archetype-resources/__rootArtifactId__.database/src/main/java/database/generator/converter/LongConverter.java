#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator.converter;

import java.math.BigDecimal;
import java.util.Optional;

import org.jooq.Converter;

/**
 * <h3>{@link LongConverter}</h3>
 */
public class LongConverter implements Converter<Long, BigDecimal> {
  private static final long serialVersionUID = 1L;

  @Override
  public BigDecimal from(Long databaseObject) {
    return Optional.ofNullable(databaseObject).map(dod -> BigDecimal.valueOf(databaseObject)).orElse(null);
  }

  @Override
  public Long to(BigDecimal userObject) {
    return Optional.ofNullable(userObject)
        .filter(uo -> {
          if (uo.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Cannot convert '" + userObject + "' to long.");
          }
          return true;
        })
        .map(uo -> uo.longValue()).orElse(null);
  }

  @Override
  public Class<Long> fromType() {
    return Long.class;
  }

  @Override
  public Class<BigDecimal> toType() {
    return BigDecimal.class;
  }
}
