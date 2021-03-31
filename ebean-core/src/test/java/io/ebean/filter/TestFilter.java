package io.ebean.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.ebean.DB;
import org.junit.Assert;
import org.junit.Test;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;

import io.ebean.Ebean;
import io.ebean.Filter;

public class TestFilter {

  @Test
  public void test_filter_with_enum() {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();

    Filter<Order> filter = DB.filter(Order.class);
    List<Order> newOrders = filter.eq("status", Order.Status.NEW).filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }

  @Test
  public void test_is_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    Filter<Order> filter = DB.filter(Order.class);
    List<Order> newOrders = filter.isNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(2);
  }

  @Test
  public void test_is_not_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    Filter<Order> filter = DB.filter(Order.class);
    List<Order> newOrders = filter.isNotNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }

  @Test
  public void test_not_is_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    Filter<Order> filter = DB.filter(Order.class);
    List<Order> newOrders = filter.not().isNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }

  @Test
  public void test_not_is_not_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    Filter<Order> filter = DB.filter(Order.class);
    List<Order> newOrders = filter.not().isNotNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(2);
  }

}
