package org.tests.basic.lob;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Query;
import org.tests.model.basic.EBasicClobFetchEager;
import org.ebeantest.LoggedSqlCollector;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestBasicClobFetchEager extends BaseTestCase {

  @Test
  public void test() {

    EBasicClobFetchEager entity = new EBasicClobFetchEager();
    entity.setName("test");
    entity.setDescription("initialClobValue");
    EbeanServer server = Ebean.getServer(null);
    server.save(entity);


    String expectedSql = "select t0.id, t0.name, t0.title, t0.description, t0.last_update from ebasic_clob_fetch_eager t0 where t0.id = ?";
    String loggedExpectedSql = "select t0.id, t0.name, t0.title, t0.description, t0.last_update from " + SCHEMA_PREFIX + "ebasic_clob_fetch_eager t0 where t0.id = ?";
    
    // Clob included in fetch as FetchType.EAGER set by annotation
    Query<EBasicClobFetchEager> defaultQuery = Ebean.find(EBasicClobFetchEager.class).setId(entity.getId());
    defaultQuery.findUnique();
    String sql = trimSql(sqlOf(defaultQuery), 6);

    assertThat(sql).contains(expectedSql);


    LoggedSqlCollector.start();

    // Same as previous query - clob included by default based on annotation
    Ebean.find(EBasicClobFetchEager.class, entity.getId());

    // Assert query same as previous ...
    List<String> loggedSql = LoggedSqlCollector.stop();
    Assert.assertEquals(1, loggedSql.size());
    assertThat(trimSql(loggedSql.get(0), 6)).contains(loggedExpectedSql);


    // Explicitly select * including Clob
    Query<EBasicClobFetchEager> explicitQuery = Ebean.find(EBasicClobFetchEager.class).setId(entity.getId()).select("*");

    explicitQuery.findUnique();
    sql = sqlOf(explicitQuery, 6);

    assertThat(sql).contains(expectedSql);

    // Update description to test refresh

    EBasicClobFetchEager updateBean = new EBasicClobFetchEager();
    updateBean.setId(entity.getId());
    updateBean.setDescription("modified");
    Ebean.update(updateBean);


    // Test refresh function

    Assert.assertEquals("initialClobValue", entity.getDescription());

    LoggedSqlCollector.start();

    // Refresh query includes all properties
    server.refresh(entity);

    // Assert all properties fetched in refresh
    loggedSql = LoggedSqlCollector.stop();
    Assert.assertEquals(1, loggedSql.size());
    assertThat(trimSql(loggedSql.get(0), 6)).contains(loggedExpectedSql);
    Assert.assertEquals("modified", entity.getDescription());

  }

}
