package io.ebean.dbmigration.model.build;


import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.config.DbConstraintNaming;
import io.ebean.dbmigration.ddlgeneration.platform.DefaultConstraintMaxLength;
import io.ebean.dbmigration.model.MTable;
import io.ebean.dbmigration.model.ModelContainer;
import io.ebean.dbmigration.model.visitor.VisitAllUsing;
import io.ebeaninternal.api.SpiEbeanServer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelBuildBeanVisitorTest extends BaseTestCase {

  @Test
  public void test() {

    SpiEbeanServer defaultServer = (SpiEbeanServer) Ebean.getDefaultServer();

    ModelContainer model = new ModelContainer();

    DbConstraintNaming constraintNaming = defaultServer.getServerConfig().getConstraintNaming();

    DefaultConstraintMaxLength maxLength = new DefaultConstraintMaxLength(60);
    ModelBuildContext ctx = new ModelBuildContext(model, constraintNaming, maxLength, true);

    ModelBuildBeanVisitor addTable = new ModelBuildBeanVisitor(ctx);

    new VisitAllUsing(addTable, defaultServer).visitAllBeans();

    MTable item = model.getTable(SCHEMA_ALIAS + "item");

    assertThat(item).isNotNull();
    assertThat(item.primaryKeyColumns()).hasSize(2);

    MTable customer = model.getTable(SCHEMA_ALIAS + "o_customer");
    assertThat(customer).isNotNull();
  }
}
