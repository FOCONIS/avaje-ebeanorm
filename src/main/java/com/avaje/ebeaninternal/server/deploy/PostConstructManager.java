package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.event.BeanPostConstruct;
import com.avaje.ebean.event.BeanPostLoad;
import com.avaje.ebeaninternal.server.core.bootup.BootupClasses;
import com.avaje.ebeaninternal.server.deploy.meta.DeployBeanDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Default implementation for creating BeanControllers.
 */
public class PostConstructManager {

  private static final Logger logger = LoggerFactory.getLogger(PostConstructManager.class);

  private final List<BeanPostConstruct> list;

  public PostConstructManager(BootupClasses bootupClasses) {
    this.list = bootupClasses.getBeanPostConstructors();
  }

  public int getRegisterCount() {
    return list.size();
  }

  /**
   * Register BeanPostLoad listeners for a given entity type.
   */
  public void addPostConstruct(DeployBeanDescriptor<?> deployDesc) {

    for (int i = 0; i < list.size(); i++) {
      BeanPostConstruct c = list.get(i);
      if (c.isRegisterFor(deployDesc.getBeanType())) {
        logger.debug("BeanPostLoad on[" + deployDesc.getFullName() + "] " + c.getClass().getName());
        deployDesc.addPostConstruct(c);
      }
    }
  }

}
