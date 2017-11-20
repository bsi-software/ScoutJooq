#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.sql;

import org.eclipse.scout.rt.platform.ApplicationScoped;

@ApplicationScoped
public interface IDataStoreService {

  void drop();

  void create();
}
