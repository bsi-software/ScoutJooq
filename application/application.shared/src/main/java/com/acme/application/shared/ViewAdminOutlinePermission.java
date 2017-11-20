package com.acme.application.shared;

import java.security.BasicPermission;

public class ViewAdminOutlinePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ViewAdminOutlinePermission() {
    super(ViewAdminOutlinePermission.class.getSimpleName());
  }
}
