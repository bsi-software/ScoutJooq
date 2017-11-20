package com.acme.application.shared.role;

import java.security.BasicPermission;

public class CreateRolePermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public CreateRolePermission() {
    super(CreateRolePermission.class.getSimpleName());
  }

}
