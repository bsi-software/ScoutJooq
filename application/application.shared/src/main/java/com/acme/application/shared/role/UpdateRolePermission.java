package com.acme.application.shared.role;

import java.security.BasicPermission;

public class UpdateRolePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public UpdateRolePermission() {
    super(UpdateRolePermission.class.getSimpleName());
  }
}
