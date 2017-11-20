package com.acme.application.server.sql;

import org.eclipse.scout.rt.platform.ApplicationScoped;

@ApplicationScoped
public interface IDataStoreService {

  void drop();

  void create();
}
