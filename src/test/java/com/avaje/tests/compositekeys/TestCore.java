package com.avaje.tests.compositekeys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.util.List;

import org.junit.*;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.avaje.tests.compositekeys.db.Item;
import com.avaje.tests.compositekeys.db.ItemKey;
import com.avaje.tests.compositekeys.db.Region;
import com.avaje.tests.compositekeys.db.RegionKey;
import com.avaje.tests.compositekeys.db.SubType;
import com.avaje.tests.compositekeys.db.SubTypeKey;
import com.avaje.tests.compositekeys.db.Type;
import com.avaje.tests.compositekeys.db.TypeKey;

/**
 * Test some of the Avaje core functionality in conjunction with composite keys.
 * like:
 * <ul>
 * <li>write</li>
 * <li>find</li>
 * </ul>
 */
public class TestCore extends BaseTestCase {

  private Transaction testCaseTxn;

  @Before
  public void setUp() throws Exception {
    
    assumeFalse("Skipping test because key feature in setUp not yet supported for MS SQL Server.", isMsSqlServer());

    Ebean.createUpdate(Item.class, "delete from Item").execute();
    Ebean.createUpdate(Region.class, "delete from Region").execute();
    Ebean.createUpdate(Type.class, "delete from Type").execute();
    Ebean.createUpdate(SubType.class, "delete from SubType").execute();

    testCaseTxn = server().beginTransaction();

    SubType subType = new SubType();
    SubTypeKey subTypeKey = new SubTypeKey();
    subTypeKey.setSubTypeId(1);
    subType.setKey(subTypeKey);
    subType.setDescription("ANY SUBTYPE");
    server().save(subType);

    Type type = new Type();
    TypeKey typeKey = new TypeKey();
    typeKey.setCustomer(1);
    typeKey.setType(10);
    type.setKey(typeKey);
    type.setDescription("Type Old-Item - Customer 1");
    type.setSubType(subType);
    server().save(type);

    type = new Type();
    typeKey = new TypeKey();
    typeKey.setCustomer(2);
    typeKey.setType(10);
    type.setKey(typeKey);
    type.setDescription("Type Old-Item - Customer 2");
    type.setSubType(subType);
    server().save(type);

    Region region = new Region();
    RegionKey regionKey = new RegionKey();
    regionKey.setCustomer(1);
    regionKey.setType(500);
    region.setKey(regionKey);
    region.setDescription("Region West - Customer 1");
    server().save(region);

    region = new Region();
    regionKey = new RegionKey();
    regionKey.setCustomer(2);
    regionKey.setType(500);
    region.setKey(regionKey);
    region.setDescription("Region West - Customer 2");
    server().save(region);

    Item item = new Item();
    ItemKey itemKey = new ItemKey();
    itemKey.setCustomer(1);
    itemKey.setItemNumber("ITEM1");
    item.setKey(itemKey);
    item.setUnits("P");
    item.setDescription("Fancy Car - Customer 1");
    item.setRegion(500);
    item.setType(10);
    server().save(item);

    item = new Item();
    itemKey = new ItemKey();
    itemKey.setCustomer(2);
    itemKey.setItemNumber("ITEM1");
    item.setKey(itemKey);
    item.setUnits("P");
    item.setDescription("Another Fancy Car - Customer 2");
    item.setRegion(500);
    item.setType(10);
    server().save(item);

    testCaseTxn.commit();
  }
  
  @After
  public void tearDown() {
    if (testCaseTxn != null && testCaseTxn.isActive()) {
      // transaction left running after the test, rollback it to make
      // the environment ready for the next test
      testCaseTxn.rollback();
    }
  }

  @Test
  public void testFind() {

    assumeFalse("Skipping test because key feature in setUp not yet supported for MS SQL Server.", isMsSqlServer());

    List<Item> items = server().find(Item.class).findList();

    assertNotNull(items);
    assertEquals(2, items.size());

    Query<Item> qItems = server().find(Item.class);
    // qItems.where(Expr.eq("key.customer", Integer.valueOf(1)));

    // I want to discourage the direct use of Expr
    qItems.where().eq("key.customer", Integer.valueOf(1));
    items = qItems.findList();

    assertNotNull(items);
    assertEquals(1, items.size());
  }

  /**
   * This partially loads the item and then lazy loads the ManyToOne assoc.
   */
  @Test  
  public void testDoubleLazyLoad() {

    assumeFalse("Skipping test because key feature in setUp not yet supported for MS SQL Server.", isMsSqlServer());

    ItemKey itemKey = new ItemKey();
    itemKey.setCustomer(2);
    itemKey.setItemNumber("ITEM1");

    Item item = server()
        .find(Item.class).select("description").where().idEq(itemKey).findUnique();
    assertNotNull(item);
    assertNotNull(item.getUnits());
    assertEquals("P", item.getUnits());

    Type type = item.getEType();
    assertNotNull(type);
    assertNotNull(type.getDescription());

    SubType subType = type.getSubType();
    assertNotNull(subType);
    assertNotNull(subType.getDescription());
  }

  @Test
  public void testEmbeddedWithOrder() {

    assumeFalse("Skipping test because key feature in setUp not yet supported for MS SQL Server.", isMsSqlServer());

    List<Item> items = server()
        .find(Item.class).order("auditInfo.created asc, type asc").findList();

    assertNotNull(items);
    assertEquals(2, items.size());
  }

  @Test
  public void testFindAndOrderByEType() {

    assumeFalse("Skipping test because key feature in setUp not yet supported for MS SQL Server.", isMsSqlServer());

    List<Item> items = server().find(Item.class).order("eType").findList();

    assertNotNull(items);
    assertEquals(2, items.size());
  }
}