#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator.converter;

import java.util.Optional;
import java.util.UUID;

import org.jooq.Converter;

// TODO fix UUID conversion (it does not work as intended)
public class UUIDConverter implements Converter<String, UUID> {

  private static final long serialVersionUID = 1L;

  @Override
  public UUID from(String databaseObject) {
    return Optional.ofNullable(databaseObject).map(dod -> UUID.fromString(databaseObject)).orElse(null);
  }

  @Override
  public String to(UUID userObject) {
    return Optional.ofNullable(userObject).map(uo -> userObject.toString()).orElse(null);
  }

  @Override
  public Class<String> fromType() {
    return String.class;
  }

  @Override
  public Class<UUID> toType() {
    return UUID.class;
  }
}
