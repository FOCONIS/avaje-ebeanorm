package com.avaje.ebean;

import com.avaje.ebean.config.dbplatform.DatabasePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.tests.model.basic.Country;
import org.avaje.agentloader.AgentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTestCase {
  
  protected static Logger logger = LoggerFactory.getLogger(BaseTestCase.class);
  
  static {
    logger.debug("... preStart");
    if (!AgentLoader.loadAgentFromClasspath("ebean-agent","debug=1;packages=com.avaje.tests,org.avaje.test")) {
      logger.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
    }
  }

  /**
   * MS SQL Server does not allow setting explicit values on identity columns
   * so tests that do this need to be skipped for SQL Server.
   */
  public boolean isMsSqlServer() {
    SpiEbeanServer spi = (SpiEbeanServer)Ebean.getDefaultServer();
    return spi.getDatabasePlatform().getName().startsWith("mssqlserver");
  }

  public boolean isH2() {
    SpiEbeanServer spi = (SpiEbeanServer)Ebean.getDefaultServer();
    return spi.getDatabasePlatform().getName().equals("h2");
  }

  public boolean isPostgres() {
    SpiEbeanServer spi = (SpiEbeanServer)Ebean.getDefaultServer();
    return spi.getDatabasePlatform().getName().equals("postgres");
  }

  /**
   * Wait for the L2 cache to propagate changes post-commit.
   */
  protected void awaitL2Cache() {
    // do nothing, used to thread sleep
  }

  protected <T> BeanDescriptor<T> getBeanDescriptor(Class<T> cls) {
    return spiEbeanServer().getBeanDescriptor(cls);
  }

  protected SpiEbeanServer spiEbeanServer() {
    return (SpiEbeanServer) Ebean.getDefaultServer();
  }

  protected EbeanServer server() {
    return Ebean.getDefaultServer();
  }

  protected void loadCountryCache() {

    Ebean.find(Country.class)
        .setLoadBeanCache(true)
        .findList();
  }
  
  protected DatabasePlatform getDatabasePlatform() {
    return ((SpiEbeanServer)Ebean.getDefaultServer()).getDatabasePlatform();
  }
  
  protected String quoteValue(String value) {
    return getDatabasePlatform().quoteValue(value);
  }
}
