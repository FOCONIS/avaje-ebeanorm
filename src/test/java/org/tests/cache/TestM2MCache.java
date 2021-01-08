package org.tests.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.tests.model.cache.M2MCacheChild;
import org.tests.model.cache.M2MCacheMaster;

import io.ebean.BaseTestCase;
import io.ebean.DB;

public class TestM2MCache extends BaseTestCase {

  @Test
  public void testM2MWithCache() throws Exception {
    M2MCacheChild cld = new M2MCacheChild();
    cld.setName("blah");
    cld.setId(1);
    DB.save(cld);

    M2MCacheMaster cfg = new M2MCacheMaster();
    cfg.setId(42);
    cfg.getSet1().add(cld);
    cfg.getSet2().add(cld);
    DB.save(cfg);
    DB.save(cfg);

    DB.find(M2MCacheMaster.class, 42);
    M2MCacheMaster cfg1 = DB.find(M2MCacheMaster.class, 42);

    cfg1.getSet1().size();
    cfg1.getSet2().size();
    // assertThat(cfg1.getSet2().iterator().next().getName()).isEqualTo("blah");
    assertThat(cfg1.getSet1().iterator().next().getName()).isEqualTo("blah");

  }
}
