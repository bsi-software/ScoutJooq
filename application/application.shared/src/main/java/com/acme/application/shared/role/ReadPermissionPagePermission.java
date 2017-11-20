package com.acme.application.shared.role;

import java.security.BasicPermission;

public class ReadPermissionPagePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadPermissionPagePermission() {
    super(ReadPermissionPagePermission.class.getSimpleName());
  }

}
