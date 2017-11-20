package com.acme.application.database.generator.converter;

import java.util.Date;
import java.util.Optional;

import org.jooq.Converter;

/**
 * <h3>{@link DateConverter}</h3>
 */
public class DateConverter implements Converter<java.sql.Date, Date> {

  private static final long serialVersionUID = 1L;

  @Override
  public Date from(java.sql.Date databaseObject) {
    return Optional.ofNullable(databaseObject).map(dod -> new Date(dod.getTime())).orElse(null);
  }

  @Override
  public java.sql.Date to(Date userObject) {
    return Optional.ofNullable(userObject).map(ud -> new java.sql.Date(ud.getTime())).orElse(null);
  }

  @Override
  public Class<java.sql.Date> fromType() {
    return java.sql.Date.class;
  }

  @Override
  public Class<Date> toType() {
    return Date.class;
  }
}
