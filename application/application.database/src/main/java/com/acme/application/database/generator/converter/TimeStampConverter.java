package com.acme.application.database.generator.converter;

import java.util.Date;
import java.util.Optional;

import org.jooq.Converter;

/**
 * <h3>{@link TimeStampConverter}</h3>
 */
public class TimeStampConverter implements Converter<java.sql.Timestamp, Date> {

  private static final long serialVersionUID = 1L;

  @Override
  public Date from(java.sql.Timestamp databaseObject) {
    return Optional.ofNullable(databaseObject).map(dod -> new Date(dod.getTime())).orElse(null);
  }

  @Override
  public java.sql.Timestamp to(Date userObject) {
    return Optional.ofNullable(userObject).map(ud -> new java.sql.Timestamp(ud.getTime())).orElse(null);
  }

  @Override
  public Class<java.sql.Timestamp> fromType() {
    return java.sql.Timestamp.class;
  }

  @Override
  public Class<Date> toType() {
    return Date.class;
  }
}
