package com.avaje.tests.model.basic.xtra;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.config.PersistBatch;
import org.avaje.ebeantest.LoggedSqlCollector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

public class TestInsertBatchWithDifferentRootTypes extends BaseTestCase {

  @Test
  public void testDifferRootTypes() {

    assumeFalse("Skipping test because batching not yet supported for MS SQL Server.",
        isMsSqlServer());

    LoggedSqlCollector.start();
    Transaction txn = Ebean.beginTransaction();
    try {
      txn.setBatch(PersistBatch.ALL);

      EdParent parent = new EdParent();
      parent.setName("MyComputer");

      EdChild child = new EdChild();
      child.setName("Harddisk 123");
      child.setParent(parent);
      ArrayList<EdChild> children = new ArrayList<EdChild>();
      children.add(child);
      parent.setChildren(children);

      Ebean.save(parent);

      EdExtendedParent extendedParent = new EdExtendedParent();
      extendedParent.setName("My second computer");
      extendedParent.setExtendedName("Multimedia");

      child = new EdChild();
      child.setName("DVBS Card");
      children = new ArrayList<EdChild>();
      children.add(child);
      extendedParent.setChildren(children);

      // nothing flushed yet
      List<String> loggedSql0 = LoggedSqlCollector.start();
      assertEquals(0, loggedSql0.size());

      // causes a flush as EdExtendedParent is different from EdParent
      Ebean.save(extendedParent);

      // insert statements for EdParent
      List<String> loggedSql1 = LoggedSqlCollector.start();
      assertEquals(2, loggedSql1.size());

      Ebean.commitTransaction();

      // insert statements for EdExtendedParent
      List<String> loggedSql2 = LoggedSqlCollector.start();
      assertEquals(2, loggedSql2.size());

    } finally {
      Ebean.endTransaction();
    }
  }

}
