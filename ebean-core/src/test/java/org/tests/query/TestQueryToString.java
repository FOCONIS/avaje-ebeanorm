package org.tests.query;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Ebean;
import io.ebean.Filter;
import io.ebean.Query;
import org.junit.Test;
import org.tests.model.basic.CKeyParent;
import org.tests.model.basic.ResetBasicData;

import static org.assertj.core.api.Assertions.assertThat;

public class TestQueryToString extends BaseTestCase {

  @Test
  public void testToString() {

    ResetBasicData.reset();

    Query<CKeyParent> sq = DB.createQuery(CKeyParent.class).select("id.oneKey").alias("st0").setAutoTune(false)
      .where().eq("name", "bla").raw("st0.name = t0.name").query();

    Filter<CKeyParent> pq1 = DB.filter(CKeyParent.class).in("id.oneKey", sq);
    Filter<CKeyParent> pq2 = DB.filter(CKeyParent.class).in("id.oneKey", sq);

    final String oldToString = pq2.toString();
    assertThat(pq1).hasToString(oldToString);

    Query<CKeyParent> q1 = DB.find(CKeyParent.class);
    pq1.applyTo(q1.where());
    q1.findList();
    assertThat(pq1)
      .hasToString(pq2.toString())
      .hasToString(oldToString);
  }

}
