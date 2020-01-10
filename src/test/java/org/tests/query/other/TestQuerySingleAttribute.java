package org.tests.query.other;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tests.inherit.ChildA;
import org.tests.inherit.Data;
import org.tests.inherit.EUncle;
import org.tests.lazyforeignkeys.MainEntity;
import org.tests.lazyforeignkeys.MainEntityRelation;
import org.tests.model.basic.Contact;
import org.tests.model.basic.Customer;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;
import org.tests.model.basic.VwCustomer;
import org.tests.o2m.OmBasicParent;

import io.ebean.BaseTestCase;
import io.ebean.CountDistinctOrder;
import io.ebean.CountedValue;
import io.ebean.DB;
import io.ebean.Ebean;
import io.ebean.Query;

public class TestQuerySingleAttribute extends BaseTestCase {

  @Test
  public void exampleUsage() {

    ResetBasicData.reset();

    List<String> names =
      Ebean.find(Customer.class)
        .setDistinct(true)
        .select("name")
        .where().eq("status", Customer.Status.NEW)
        .orderBy().asc("name")
        .setMaxRows(100)
        .findSingleAttributeList();

    assertThat(names).isNotNull();
  }

  @Test
  public void exampleUsage_otherType() {

    ResetBasicData.reset();

    List<Date> dates =
      Ebean.find(Customer.class)
        .setDistinct(true)
        .select("anniversary")
        .where().isNotNull("anniversary")
        .orderBy().asc("anniversary")
        .findSingleAttributeList();

    assertThat(dates).isNotNull();
  }

  @Test
  public void withOrderBy() {

    Query<Customer> query =
      Ebean.find(Customer.class)
        .setDistinct(true)
        .select("name")
        .where().eq("status", Customer.Status.NEW)
        .orderBy().asc("name");

    query.findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select distinct t0.name from o_customer t0 where t0.status = ? order by t0.name");
  }

  @Test
  public void basic() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).select("name");

    List<String> names = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select t0.name from o_customer t0");
    assertThat(names).isNotNull();
  }

  @Test
  public void findSingleAttribute() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .select("name")
      .where().eq("id", 1).query();

    String name = query.findSingleAttribute();

    assertThat(sqlOf(query)).contains("select t0.name from o_customer t0");
    assertThat(name).isNotNull();
  }

  @Test
  public void findSingleAttributeList_with_join_column() {

    ResetBasicData.reset();



    Query<MainEntityRelation> query = Ebean.find(MainEntityRelation.class)
      .fetch("entity1", "attr1")
      .setDistinct(true)
      .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
      .where().query();

    List<CountedValue<String>> attr1list = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct r1.attribute_1, count(*) cnt"
        + " from (select t1.attr1 attribute_1 from main_entity_relation t0 left join main_entity t1 on t1.id = t0.id1 ) r1"
        + " group by r1.attribute_1"
        + " order by count(*) desc, r1.attribute_1");
    assertThat(attr1list).isNotNull();
    assertThat(attr1list).hasSize(2);
    assertThat(attr1list.get(0).getValue()).isEqualTo("a1");
    assertThat(attr1list.get(0).getCount()).isEqualTo(2l);
    assertThat(attr1list.get(1).getValue()).isEqualTo("a2");
    assertThat(attr1list.get(1).getCount()).isEqualTo(1l);


  }

  @Test
  public void findSingleAttributesVariousSelection1() {
    Query<MainEntityRelation> query = Ebean.find(MainEntityRelation.class)
        .fetch("entity1", "attr1")
        .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
        .where().query();

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt"
        + " from (select t1.attr1 attribute_1 from main_entity_relation t0 left join main_entity t1 on t1.id = t0.id1 ) r1"
        + " group by r1.attribute_1"
        + " order by count(*) desc, r1.attribute_1");

  }
  @Test
  public void findSingleAttributesVariousSelection2() {
    Query<MainEntityRelation> query = Ebean.find(MainEntityRelation.class)
        .select("attr1")
        .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
        .where().query();

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt"
        + " from (select t0.attr1 attribute_1 from main_entity_relation t0) r1"
        + " group by r1.attribute_1"
        + " order by count(*) desc, r1.attribute_1");

  }
  @Test
  public void findSingleAttributesVariousSelection3() {
    Query<MainEntityRelation> query = Ebean.find(MainEntityRelation.class)
        .select("id")
        .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
        .where().query();

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt"
        + " from (select t0.id attribute_1 from main_entity_relation t0) r1"
        + " group by r1.attribute_1"
        + " order by count(*) desc, r1.attribute_1");

  }

  @Test
  public void findSingleAttributesVariousSelection4() {
    Query<MainEntityRelation> query = Ebean.find(MainEntityRelation.class)
        .fetch("entity1", "id")
        .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
        .where().query();

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt"
        + " from (select t1.id attribute_1 from main_entity_relation t0 left join main_entity t1 on t1.id = t0.id1 ) r1"
        + " group by r1.attribute_1"
        + " order by count(*) desc, r1.attribute_1");

  }

  @Test
  public void findSingleAttributesVariousSelection5() {
    Query<OmBasicParent> query = Ebean.find(OmBasicParent.class)
        .fetch("children", "name")
        .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
        .where().query();

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt"
        + " from (select t1.name attribute_1 from om_basic_parent t0 left join om_basic_child t1 on t1.parent_id = t0.id ) r1 "
        + "group by r1.attribute_1 order by count(*) desc, r1.attribute_1");

  }

  @Test
  public void findSingleAttribute_with_aggregate() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .select("max(name)")
      .where().gt("id", 1).query();

    String name = query.findSingleAttribute();

    assertThat(sqlOf(query)).contains("select max(t0.name) from o_customer t0");
    assertThat(name).isNotNull();
  }

  @Test
  public void findSingleAttribute_viaExpression() {

    ResetBasicData.reset();

    String name = Ebean.find(Customer.class)
      .select("name")
      .where().eq("id", 1)
      .findSingleAttribute();

    assertThat(name).isNotNull();
  }

  @Test
  public void distinctAndWhere() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .select("name")
      .where().eq("status", Customer.Status.NEW)
      .query();

    List<String> names = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.name from o_customer t0 where t0.status = ?");
    assertThat(names).isNotNull();
  }

  @Test
  public void distinctWhereWithJoin() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .select("name")
      .where().eq("status", Customer.Status.NEW)
      .istartsWith("billingAddress.city", "auck")
      .query();

    List<String> names = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.name from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id  where t0.status = ? and lower(t1.city) like ");
    assertThat(names).isNotNull();
  }


  @Test
  public void queryPlan_expect_differentPlans() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).select("name");
    query.findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select t0.name from o_customer t0");

    Query<Customer> query2 = Ebean.find(Customer.class).select("name");
    query2.findList();
    assertThat(sqlOf(query2, 1)).contains("select t0.id, t0.name from o_customer t0");
  }

  @Test
  public void distinctOnIdProperty() {
    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .select("id")
      .setMaxRows(100);

    List<String> ids = query.findSingleAttributeList();
    if (isSqlServer()) {
      assertThat(sqlOf(query)).contains("select distinct top 100 t0.id from o_customer t0");
    } else if (isOracle()) {
      assertThat(sqlOf(query)).contains("from ( select distinct t0.id from o_customer t0");
      assertThat(sqlOf(query)).contains("where rownum <= 100");
    } else {
      assertThat(sqlOf(query)).contains("select distinct t0.id from o_customer t0 limit 100");
    }
    assertThat(ids).isNotEmpty();
  }

  @Test
  public void distinctWithFetch() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .fetch("billingAddress", "city");

    List<String> cities = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t1.city from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id");
    assertThat(cities).contains("Auckland").containsNull();
  }

  @Test
  public void distinctWithCascadedFetch() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .fetch("customer.billingAddress", "city");

    List<String> cities = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t2.city from contact t0 join o_customer t1 on t1.id = t0.customer_id  left join o_address t2 on t2.id = t1.billing_address_id");
    assertThat(cities).contains("Auckland").containsNull();
  }

  @Test
  public void distinctSelectOnInheritedBean() {

    ResetBasicData.reset();

    Query<ChildA> query = Ebean.find(ChildA.class)
      .setDistinct(true)
      .select("more");

    query.findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select distinct t0.more from rawinherit_parent t0 where t0.type = 'A'");
  }

  @Test
  public void distinctFetchManyToOneInheritedBean() {

    ResetBasicData.reset();

    Query<EUncle> query = Ebean.find(EUncle.class)
      .setDistinct(true)
      .fetch("parent", "more");

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t1.more from rawinherit_uncle t0 join rawinherit_parent t1 on t1.id = t0.parent_id");

  }

  // hmm - same problem when not using distinct
  @Test
  public void findSingleOnIdProperty() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .select("id")
      .setMaxRows(100);

    List<String> ids = query.findSingleAttributeList();
    if (isSqlServer()) {
      assertThat(sqlOf(query)).contains("select top 100 t0.id from o_customer t0");
    } else if (isOracle()) {
      assertThat(sqlOf(query)).contains("where rownum <= 100");
    } else {
      assertThat(sqlOf(query)).contains("select t0.id from o_customer t0 limit 100");
    }
    assertThat(ids).isNotEmpty();
  }

  @Test
  public void findSingleWithFetch() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .fetch("billingAddress", "city");

    List<String> cities = query.findSingleAttributeList();

    assertThat(cities).contains("Auckland").containsNull();
    assertThat(sqlOf(query)).contains("select t1.city from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id");
  }

  @Test
  public void findSingleWithFetchOnView() {

    ResetBasicData.reset();

    Query<VwCustomer> query = Ebean.find(VwCustomer.class)
      .fetch("billingAddress", "city");

    List<String> cities = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select t1.city from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id");
    assertThat(cities).contains("Auckland").containsNull();
  }

  @Test
  public void findSingleSelectOnInheritedBean() {

    ResetBasicData.reset();

    Query<ChildA> query = Ebean.find(ChildA.class)
      .select("more");

    query.findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select t0.more from rawinherit_parent t0 where t0.type = 'A'");

  }

  @Test
  public void findSingleFetchManyToOneInheritedBean() {

    ResetBasicData.reset();

    Query<EUncle> query = Ebean.find(EUncle.class)
      .fetch("parent", "more");

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select t1.more from rawinherit_uncle t0 join rawinherit_parent t1 on t1.id = t0.parent_id");
  }

  @Test
  public void findSingleFetchManyToOneInheritedBean_viaEbeanServer() {

    ResetBasicData.reset();

    Query<EUncle> query = Ebean.find(EUncle.class)
      .fetch("parent", "more");

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select t1.more from rawinherit_uncle t0 join rawinherit_parent t1 on t1.id = t0.parent_id");
  }

  @Test
  @Ignore //don't know if ebean can handle this on many to many, as this means that the cartesian product is generated
  public void distinctFetchManyToManyInheritedBean() {

    ResetBasicData.reset();

    Query<Data> query = Ebean.find(Data.class)
      .setDistinct(true)
      .fetch("parents", "more")
      .setMaxRows(100);

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.more from rawinherit_data t0 "
      + "join rawinherit_parent_rawinherit_data t1 on t0.id = t1.rawinherit_data_id "
      + "join parent t2 on t1.rawinherit_parent_id = t2.id");
  }

  @Test
  public void distinctWithOrderByPk() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .select("customer")
      .orderBy().desc("customer");

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.customer_id from contact t0 order by t0.customer_id desc");
  }

  @Test
  public void distinctWithOrderByPkWithId() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .select("customer.id")
      .orderBy().desc("customer.id");

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.customer_id from contact t0 order by t0.customer_id desc");
  }

  @Test
  public void distinctWithCascadedFetchOrderByPk() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .fetch("customer", "billingAddress")
      .orderBy().desc("customer.billingAddress");

    query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t1.billing_address_id from contact t0 join o_customer t1 on t1.id = t0.customer_id  order by t1.billing_address_id desc");
  }

  @Test
  public void distinctWithOrderByPkAndQuery() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .fetch("customer", "billingAddress")
      .where().eq("customer.billingAddress.city", "Auckland")
      .orderBy().desc("customer.billingAddress.id");

    List<Integer> ids = query.findSingleAttributeList();
    assertThat(ids).isNotEmpty();

    assertThat(sqlOf(query)).contains("select distinct t1.billing_address_id from contact t0 "
      + "join o_customer t1 on t1.id = t0.customer_id  "  // two spaces!
      + "left join o_address t2 on t2.id = t1.billing_address_id  "
      + "where t2.city = ? "
      + "order by t1.billing_address_id desc");
  }

  @Test
  public void distinctWithCascadedFetchOrderByPkAndQuery() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .fetch("customer", "billingAddress")
      .where().eq("customer.billingAddress.city", "Auckland")
      .orderBy().desc("customer.billingAddress.id");

    List<Short> ids = query.findSingleAttributeList();
    assertThat(ids).isNotEmpty();

    assertThat(sqlOf(query)).contains("select distinct t1.billing_address_id from contact t0 "
      + "join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.billing_address_id  "
      + "where t2.city = ? "
      + "order by t1.billing_address_id desc");
  }

  @Test
  public void distinctWithCascadedFetchOrderByPkAndQuery3() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .fetch("customer", "billingAddress")
      .where().eq("customer.billingAddress.city", "Auckland")
      .orderBy().desc("customer.billingAddress.id");

    List<Integer> ids = query.findSingleAttributeList();
    assertThat(ids).isNotEmpty();

    assertThat(sqlOf(query)).contains("select distinct t1.billing_address_id from contact t0 "
      + "join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.billing_address_id  "
      + "where t2.city = ? "
      + "order by t1.billing_address");
  }

  @Test
  public void distinctWithCascadedFetchOrderByPkAndQuery2() {

    ResetBasicData.reset();

    Query<Contact> query = Ebean.find(Contact.class)
      .setDistinct(true)
      .fetch("customer", "billingAddress")
      .where().eq("customer.shippingAddress.city", "Auckland") // query on shippingAddress
      .orderBy().desc("customer.billingAddress.id");

    List<Short> ids = query.findSingleAttributeList();
    assertThat(ids).isNotEmpty();

    assertThat(sqlOf(query)).contains("select distinct t1.billing_address_id from contact t0 "
      + "join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.shipping_address_id  "
      + "where t2.city = ? "
      + "order by t1.billing_address_id desc");

  }

  @Test
  public void distinctWithCascadedFetchCount() {

    ResetBasicData.reset();


    int count = Ebean.find(Contact.class).findCount();
    assertThat(count).isGreaterThanOrEqualTo(12);

    Query<Contact> query = Ebean.find(Contact.class)
      .select("firstName");

    List<CountedValue<Object>> list1 = query
      .setCountDistinct(CountDistinctOrder.ATTR_ASC)

      .findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt from ("
      + "select t0.first_name attribute_1 from contact t0"
      + ") r1 group by r1.attribute_1 order by r1.attribute_1");
    assertThat(list1.get(0)).isInstanceOf(CountedValue.class);
    assertThat(list1.toString()).isEqualTo("[3: Bugs1, 1: Fiona, 3: Fred1, 1: Jack, 3: Jim1, 1: Tracy]");


    query = Ebean.find(Contact.class).select("firstName");
    list1 = query
      .setCountDistinct(CountDistinctOrder.ATTR_DESC)
      .findSingleAttributeList();
    assertThat(list1.get(0)).isInstanceOf(CountedValue.class);
    assertThat(list1.toString()).isEqualTo("[1: Tracy, 3: Jim1, 1: Jack, 3: Fred1, 1: Fiona, 3: Bugs1]");

    query = Ebean.find(Contact.class).select("firstName");
    list1 = query
      .setCountDistinct(CountDistinctOrder.COUNT_ASC_ATTR_DESC)
      .findSingleAttributeList();
    assertThat(list1.get(0)).isInstanceOf(CountedValue.class);
    assertThat(list1.toString()).isEqualTo("[1: Tracy, 1: Jack, 1: Fiona, 3: Jim1, 3: Fred1, 3: Bugs1]");

    query = Ebean.find(Contact.class).fetch("customer.shippingAddress", "line1");//("firstName")
    List<CountedValue<Object>> list2 = query
      .setCountDistinct(CountDistinctOrder.ATTR_ASC)
      .findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt from ("
      + "select t2.line_1 attribute_1 "
      + "from contact t0 join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.shipping_address_id "
      + ") r1 group by r1.attribute_1 order by r1.attribute_1");
    assertThat(list2.get(0)).isInstanceOf(CountedValue.class);
    if (isPostgres() || isOracle()) {
      assertThat(list2.toString()).isEqualTo("[3: 1 Banana St, 5: 12 Apple St, 3: 15 Kumera Way, 1: null]");
    } else {
      assertThat(list2.toString()).isEqualTo("[1: null, 3: 1 Banana St, 5: 12 Apple St, 3: 15 Kumera Way]");
    }

    query = Ebean.find(Contact.class).select("firstName")
      .where().eq("customer.shippingAddress.line1", "12 Apple St").query();
    List<CountedValue<Object>> list3 = query
      .setCountDistinct(CountDistinctOrder.ATTR_ASC)
      .findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt from ("
      + "select t0.first_name attribute_1 from contact t0 "
      + "join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.shipping_address_id  where t2.line_1 = ?"
      + ") r1 group by r1.attribute_1 order by r1.attribute_1");
    assertThat(list3.get(0)).isInstanceOf(CountedValue.class);
    assertThat(list3.toString()).isEqualTo("[1: Bugs1, 1: Fiona, 1: Fred1, 1: Jim1, 1: Tracy]");


    query = Ebean.find(Contact.class).fetch("customer.billingAddress", "line1")
      .where().or()
      .ne("customer.shippingAddress.line1", "12 Apple St")
      .isNull("customer.shippingAddress.line1")
      .endOr().query();
    List<CountedValue<Object>> list4 = query
      .setCountDistinct(CountDistinctOrder.ATTR_ASC)
      .findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt from ("
      + "select t2.line_1 attribute_1 "
      + "from contact t0 join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.billing_address_id  "
      + "left join o_address t3 on t3.id = t1.shipping_address_id  "
      + "where (t3.line_1 <> ? or t3.line_1 is null)"
      + ") r1 group by r1.attribute_1 order by r1.attribute_1");
    assertThat(list4.get(0)).isInstanceOf(CountedValue.class);
    if (isPostgres() || isOracle()) {
      assertThat(list4.toString()).isEqualTo("[3: Bos town, 3: P.O.Box 1234, 1: null]");
    } else {
      assertThat(list4.toString()).isEqualTo("[1: null, 3: Bos town, 3: P.O.Box 1234]");
    }

    // Test Limiter for MSSQL
    query = Ebean.find(Contact.class).fetch("customer.billingAddress", "line1").setFirstRow(1).setMaxRows(2);
    List<CountedValue<Object>> list5 = query
      .where().isNotNull("customer.billingAddress.line1").query()
      .setCountDistinct(CountDistinctOrder.ATTR_DESC)
      .findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt from ("
      + "select t2.line_1 attribute_1 from contact t0 "
      + "join o_customer t1 on t1.id = t0.customer_id  "
      + "left join o_address t2 on t2.id = t1.billing_address_id  "
      + "where t2.line_1 is not null"
      + ") r1 group by r1.attribute_1 order by r1.attribute_1 desc ");
    if (isSqlServer()) {
      assertThat(sqlOf(query)).endsWith(" fetch next 2 rows only");
    } else if (isDb2()) {
      assertThat(query.getGeneratedSql()).endsWith("FETCH FIRST 2 ROWS ONLY");
    } else {
      assertThat(sqlOf(query)).endsWith(" limit 2 offset 1");
    }
    assertThat(list5.get(0)).isInstanceOf(CountedValue.class);
    assertThat(list5.toString()).isEqualTo("[3: P.O.Box 1234, 3: Bos town]");

    query = Ebean.find(Contact.class).select("firstName")
        .where().eq("customer.shippingAddress.line1", "12 Apple St").query();
      List<CountedValue<Object>> list6 = query
        .setCountDistinct(CountDistinctOrder.NO_ORDERING)
        .findSingleAttributeList();
      assertThat(sqlOf(query)).contains("select r1.attribute_1, count(*) cnt from ("
        + "select t0.first_name attribute_1 from contact t0 "
        + "join o_customer t1 on t1.id = t0.customer_id  "
        + "left join o_address t2 on t2.id = t1.shipping_address_id  where t2.line_1 = ?"
        + ") r1 group by r1.attribute_1")
        .doesNotContain("order by");
      assertThat(list6.get(0)).isInstanceOf(CountedValue.class);
      // Ordering is not required/deterministic
      // assertThat(list6.toString()).isEqualTo("[1: Bugs1, 1: Fiona, 1: Fred1, 1: Jim1, 1: Tracy]");
  }

  public static class TestDto {
    int cnt;
    LocalDate date;
    String prefix;

    public int getCnt() {
      return cnt;
    }

    public void setCnt(int cnt) {
      this.cnt = cnt;
    }

    public LocalDate getDate() {
      return date;
    }

    public void setDate(LocalDate date) {
      this.date = date;
    }

    public String getPrefix() {
      return prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public @Nonnull String toString() {
      return "#" + cnt + ", " + prefix + ", @" + date;
    }
  }

  @Test
  public void distinctWithDtoAndAggregationCount() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).select("cast(cretime as date) as date, left(smallnote,3) as prefix")
      .setCountDistinct(CountDistinctOrder.NO_ORDERING)
      .setCountDistinctDto(TestDto.class)
      .setUseQueryCache(true);

    List<TestDto> list = query.findSingleAttributeList();

    assertThat(list).hasSize(1);

    assertThat(sqlOf(query)).isEqualTo("select r1.date, r1.prefix, count(*) cnt from ("
      + "select cast(t0.cretime as date) date, left(t0.smallnote,3) prefix from o_customer t0"
      + ") r1 group by r1.date, r1.prefix");
  }

  @Test
  public void distinctWithDtoAndAliasCount() {

    ResetBasicData.reset();

    // Brackets for lastName currently necessary to parse correctly.
    Query<Customer> query = Ebean.find(Customer.class).select("cast(cretime as date) as date, (smallnote) as prefix")
      .setCountDistinct(CountDistinctOrder.NO_ORDERING)
      .setCountDistinctDto(TestDto.class)
      .setUseQueryCache(true);

    List<TestDto> list = query.findSingleAttributeList();

    assertThat(list).hasSize(1);

    assertThat(sqlOf(query)).isEqualTo("select r1.date, r1.prefix, count(*) cnt from ("
        + "select cast(t0.cretime as date) date, (t0.smallnote) prefix from o_customer t0"
        + ") r1 group by r1.date, r1.prefix");
  }

  @Test
  public void distinctWithCastAndFetchCount() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .select("cast(cretime as date)::LocalDate as date")
      .fetch("shippingAddress", "left(city,3)::String as prefix")
      .setCountDistinct(CountDistinctOrder.NO_ORDERING)
      .setCountDistinctDto(TestDto.class)
      .setUseQueryCache(true);

    List<TestDto> list = query.findSingleAttributeList();

    assertThat(list).extracting(dto -> dto.getCnt()).containsAll(Arrays.asList(1, 3));

    assertThat(sqlOf(query)).contains("select r1.date, r1.prefix, count(*) cnt from ("
        + "select cast(t0.cretime as date) date, left(t1.city,3) prefix from o_customer t0 "
        + "left join o_address t1 on t1.id = t0.shipping_address_id "
        + ") r1 group by r1.date, r1.prefix");
  }

  @Test
  public void example() {

    ResetBasicData.reset();

    List<CountedValue<Order.Status>> orderStatusCount = Ebean.find(Order.class)
      .select("status")
      .where()
      .gt("orderDate", LocalDate.now().minusMonths(3))
      .setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC)
      .findSingleAttributeList();

    for (CountedValue<Order.Status> entry : orderStatusCount) {
      System.out.println(" count:" + entry.getCount()+" orderStatus:" + entry.getValue() );
    }
  }

  @Before
  public void setup() {
    MainEntity e1 = new MainEntity();
    e1.setId("1");
    e1.setAttr1("a1");
    DB.save(e1);

    MainEntity e2 = new MainEntity();
    e2.setId("2");
    e2.setAttr1("a2");
    DB.save(e2);

    MainEntity e3 = new MainEntity();
    e3.setId("3");
    e3.setAttr1("a1");
    DB.save(e3);

    MainEntityRelation rel = new MainEntityRelation();
    rel.setEntity1(e1);
    rel.setEntity2(e1);
    DB.save(rel);

    rel = new MainEntityRelation();
    rel.setEntity1(e2);
    rel.setEntity2(e2);
    DB.save(rel);

    rel = new MainEntityRelation();
    rel.setEntity1(e3);
    rel.setEntity2(e3);
    DB.save(rel);
  }

  @After
  public void cleanup() {
    Ebean.find(MainEntityRelation.class).delete();
    Ebean.find(MainEntity.class).delete();
  }
}
