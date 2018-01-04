#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.sql;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.server.ServerProperties;

/**
 * 
 * @author fwi
 *
 */
public abstract class AbstractJooqService {

    Logger LOG = LoggerFactory.getLogger(AbstractJooqService.class);


    private Configuration configuration = null;

    public AbstractJooqService() {
        initializeConfiguration();
    }

    /**
     * Returns a Jooq DSL context for the provided JDBC connection.
     */
    protected DSLContext getContext() {
        return DSL.using(configuration.derive(new ScoutConnectionProvider()));
    }

    protected void initializeConfiguration() {
        configuration = new DefaultConfiguration();
        configuration.set(CONFIG.getPropertyValue(ServerProperties.DialectProperty.class));
        Settings s = configuration.settings();
        //TODO Configure settings as needed.
//        s.setRenderSchema(false);
        configuration.set(s);
    }
    
    protected Configuration getConfiguration() {
        return configuration;
    }

}
