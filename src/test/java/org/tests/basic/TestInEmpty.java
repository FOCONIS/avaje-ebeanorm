package org.tests.basic;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.Query;

import org.tests.model.basic.Customer;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;

import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.junit.Assert.assertEquals;

public class TestInEmpty extends BaseTestCase {

  private static final int MAX_PARAMS = 100000;
  
  @Test
  public void test_in_empty() {
    ResetBasicData.reset();
    Query<Order> query = Ebean.find(Order.class).where().in("id", new Object[0]).gt("id", 0)
      .query();

    List<Order> list = query.findList();
    assertThat(query.getGeneratedSql()).contains("1=0");
    assertEquals(0, list.size());
  }

  @Test
  public void test_notIn_empty() {

    Query<Order> query = Ebean.find(Order.class).where().notIn("id", new Object[0]).gt("id", 0)
      .query();

    query.findList();
    assertThat(query.getGeneratedSql()).contains("1=1");
  }

  
  @Test
  public void test_in_many_integer() {
    ResetBasicData.reset();
    Object[] values = new Object[MAX_PARAMS];
    values[0] = 1;
    values[1] = 2;
    values[2] = 3;
    for (int i = 3; i < values.length; i++) {
      values[i] = -i;
    }
    Query<Order> query = Ebean.find(Order.class).where().in("id", values).le("id",4).query();

    List<Order> list = query.findList();
    assertEquals(3, list.size());
  }
  
  @Test
  public void test_in_many_date() {
    ResetBasicData.reset();
    Object[] values = new Object[MAX_PARAMS];
    
    for (int i = 0; i < values.length; i++) {
      values[i] = new Date(System.currentTimeMillis() + i * 86400000);
    }
    Query<Order> query = Ebean.find(Order.class).where().in("order_date", values).le("id",4).query();

    List<Order> list = query.findList();
    assertEquals(4, list.size());
  }
  
  @Test
  public void test_in_many_datetime() {
    ResetBasicData.reset();
    Object[] values = new Object[MAX_PARAMS];
    
    values[0] = Ebean.find(Order.class, 3).getCretime();
    
    for (int i = 1; i < values.length; i++) {
      values[i] = new Timestamp(1234);
    }
    Query<Order> query = Ebean.find(Order.class).where().in("cretime", values).le("id",4).query();

    List<Order> list = query.findList();
    assertThat(list.size()).isGreaterThanOrEqualTo(1);
  }

  
  @Test
  public void test_in_many_varchar() {
    ResetBasicData.reset();
    Object[] values = new Object[MAX_PARAMS];
    
    values[0] = "Rob";
    values[1] = "Fiona";
    for (int i = 2; i < values.length; i++) {
      values[i] = "FooBar"+i;;
    }
    Query<Customer> query = Ebean.find(Customer.class).where().in("name", values).le("id",4).query();

    List<Customer> list = query.findList();
    assertThat(list.size()).isEqualTo(2);
  }
  
  @Test
  public void test_in_many_idin() {
    ResetBasicData.reset();
    Object[] values = new Object[MAX_PARAMS];
    values[0] = 1;
    values[1] = 2;
    values[2] = 3;
    for (int i = 3; i < values.length; i++) {
      values[i] = -i;
    }
    Query<Order> query = Ebean.find(Order.class).where().idIn(values).le("id",4).query();

    List<Order> list = query.findList();
    assertEquals(3, list.size());
  }

  @Test
  public void test_in_many_delete() {
    ResetBasicData.reset();
    List<Integer> values = new ArrayList<>();
    for (int i = 0; i < MAX_PARAMS; i++) {
      values.add(-i);
    }
    server().deleteAll(Order.class, values);
    
  }
  @Test
  public void test_with_null() {
    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).where().in("anniversary", new Object[1]).le("id",4).query();

    List<Customer> list = query.findList();
    assertThat(query.getGeneratedSql()).contains(" is null");
    assertThat(list.size()).isEqualTo(1);
    
    query = Ebean.find(Customer.class).where().notIn("anniversary", new Object[1]).le("id",4).query();

    list = query.findList();
    assertThat(query.getGeneratedSql()).contains(" is not null");
    assertThat(list.size()).isEqualTo(3);
    
    query = Ebean.find(Customer.class).where().eq("anniversary", null).le("id",4).query();

    list = query.findList();
    assertThat(query.getGeneratedSql()).contains(" is null");
    assertThat(list.size()).isEqualTo(1);
    
    query = Ebean.find(Customer.class).where().ne("anniversary", null).le("id",4).query();

    list = query.findList();
    assertThat(query.getGeneratedSql()).contains(" is not null");
    assertThat(list.size()).isEqualTo(3);
    
    Object[] values = new Object[MAX_PARAMS];
    
    values[0] = new Date(110,03,14);
    values[1] = new Date(109,07,31);

  }
  @Test
  public void test_many_with_null() {
    ResetBasicData.reset();
    
    Object[] values = new Object[MAX_PARAMS];
    
    values[0] = new Date(110,03,14);
    values[1] = new Date(109,07,31);
    
    Query<Customer> query = Ebean.find(Customer.class).where().in("anniversary", values).le("id",4).query();

    List<Customer> list = query.findList();
    assertThat(query.getGeneratedSql()).contains(" is null");
    assertThat(list.size()).isEqualTo(3);
    
    query = Ebean.find(Customer.class).where().notIn("anniversary", values).le("id",4).query();

    list = query.findList();
    assertThat(query.getGeneratedSql()).doesNotContain(" is null");
    assertThat(list.size()).isEqualTo(1);
    values = new Object[] {values[0], values[1]};
    query = Ebean.find(Customer.class).where().notIn("anniversary", values).le("id",4).query();

    list = query.findList();
    assertThat(query.getGeneratedSql()).contains(" is null");
    assertThat(list.size()).isEqualTo(2);
  }
}
