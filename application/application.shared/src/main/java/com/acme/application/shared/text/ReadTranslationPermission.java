package com.acme.application.shared.text;

import java.security.BasicPermission;

public class ReadTranslationPermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadTranslationPermission() {
    super(ReadTranslationPermission.class.getSimpleName());
  }
}
