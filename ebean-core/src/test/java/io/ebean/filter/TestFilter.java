package io.ebean.filter;

import io.ebean.DB;
import io.ebean.Filter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
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

    List<Order> newOrders = DB.filter(Order.class)
      .eq("status", Order.Status.NEW)
      .filter(allOrders);

    assertThat(newOrders).hasSize(3);
  }

  @Test
  public void test_is_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    List<Order> newOrders = DB.filter(Order.class)
      .isNull("customer")
      .filter(allOrders);

    assertThat(newOrders).hasSize(2);
  }

  @Test
  public void test_is_not_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    List<Order> newOrders = DB.filter(Order.class)
      .isNotNull("customer")
      .filter(allOrders);

    assertThat(newOrders).hasSize(3);
  }

  @Test
  public void test_not_is_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    List<Order> newOrders = DB.filter(Order.class)
      .not().isNull("customer")
      .filter(allOrders);

    assertThat(newOrders).hasSize(3);
  }

  @Test
  public void test_not_is_not_null()  {

    ResetBasicData.reset();

    List<Order> allOrders = DB.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);

    List<Order> newOrders = DB.filter(Order.class)
      .not().isNotNull("customer")
      .filter(allOrders);

    assertThat(newOrders)      .hasSize(2);
  }

  @Test
  public void test_istartswith() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");

    List<Order> orders = DB.filter(Order.class)
      .icontains("customerName", "T")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .icontains("customerName", "Tom")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    List<Order> orders3 = DB.filter(Order.class)
      .icontains("customerName", "")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

  @Test
  public void test_istartswith_with_linebreak() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");

    List<Order> orders = DB.filter(Order.class)
      .icontains("customerName", "T")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .icontains("customerName", "Tom")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    List<Order> orders3 = DB.filter(Order.class)
      .icontains("customerName", "")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

  @Test
  public void test_icontains() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");

    List<Order> orders = DB.filter(Order.class)
      .icontains("customerName", "R")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .icontains("customerName", "Rob")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    List<Order> orders3 = DB.filter(Order.class)
      .icontains("customerName", "")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }
  @Test
  public void test_icontains_with_linebreak() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");

    List<Order> orders = DB.filter(Order.class)
      .icontains("customerName", "R")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .icontains("customerName", "Rob")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    List<Order> orders3 = DB.filter(Order.class)
      .icontains("customerName", "")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

  @Test
  public void test_iendswith() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");

    List<Order> orders = DB.filter(Order.class)
      .icontains("customerName", "b")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .icontains("customerName", "Rob")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    List<Order> orders3 = DB.filter(Order.class)
      .icontains("customerName", "")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

  @Test
  public void test_iendswith_with_linebreak() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");

    List<Order> orders = DB.filter(Order.class)
      .icontains("customerName", "b")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .icontains("customerName", "Rob")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);

    List<Order> orders3 = DB.filter(Order.class)
      .icontains("customerName", "")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

  @Test
  public void test_ilike() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");

    List<Order> orders = DB.filter(Order.class)
      .ilike("customerName", "%R%")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .ilike("customerName", "%Rob%")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
  }

  @Test
  public void test_ilike_with_linebreak() {
    ResetBasicData.reset();

    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");

    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");

    List<Order> orders = DB.filter(Order.class)
      .ilike("customerName", "%R%")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);

    List<Order> orders2 = DB.filter(Order.class)
      .ilike("customerName", "%Rob%")
      .filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
  }
}
