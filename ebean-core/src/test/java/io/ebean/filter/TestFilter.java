package io.ebean.filter;

import io.ebean.DB;
import io.ebean.Filter;
import org.junit.Assert;
import org.junit.Test;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

  @Test
  public void test_iContains() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Thomas Maier");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");

    Filter<Order> filter = DB.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "R").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    Filter<Order> filter2 = DB.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Rob").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    Filter<Order> filter3 = DB.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

}
