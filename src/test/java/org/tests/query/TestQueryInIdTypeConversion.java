package org.tests.query;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Ebean;
import io.ebeantest.LoggedSql;

import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;
import org.tests.model.basic.TUuidEntity;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestQueryInIdTypeConversion extends BaseTestCase {

  @Test
  public void test() {

    ResetBasicData.reset();

    List<Customer> list = Ebean.find(Customer.class).where().idIn("1", "2").findList();

    assertThat(list).hasSize(2);

  }

  @Test
  public void testUuidConversion() throws Exception {

    TUuidEntity entity1 = new TUuidEntity();
    entity1.setName("Test 1");
    DB.save(entity1);

    TUuidEntity entity2 = new TUuidEntity();
    entity2.setName("Test 2");
    DB.save(entity2);
String id = entity1.getId().toString();
    Set<String> ids = new HashSet<>();
    ids.add(id);

    LoggedSql.start();
    List<TUuidEntity> list = DB.find(TUuidEntity.class).where().idIn(ids).findList();
    assertThat(list).hasSize(1);
    System.out.println("SQL 1: " + LoggedSql.stop());

    LoggedSql.start();
    list = DB.find(TUuidEntity.class).where().eq("id",id).findList();
    assertThat(list).hasSize(1);
    System.out.println("SQL 2: " + LoggedSql.stop());

    LoggedSql.start();
    list = DB.find(TUuidEntity.class).where().in("id",ids).findList();
     assertThat(list).hasSize(1);
    System.out.println("SQL 3: " + LoggedSql.stop());
  }
}
