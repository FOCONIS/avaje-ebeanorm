package org.tests.model.inheritmany;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.Query;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMediaInheritanceJoinToMany extends BaseTestCase {

  @Test
  public void test() {

    String name = "nopic" + new Random().nextInt();

    MProfile profileWithNoPic = new MProfile();
    profileWithNoPic.setName(name);

    Ebean.save(profileWithNoPic);

    Query<MProfile> query = Ebean.find(MProfile.class).fetch("picture").where().eq("name", name).query();

    // assert we get the profile with a null picture
    MProfile profile = query.findOne();
    Assert.assertNotNull(profile);

    String generatedSql = query.getGeneratedSql();

    assertThat(generatedSql).contains("from mprofile t0 left join mmedia t1 on t1.id = t0.picture_id ");
    assertThat(generatedSql).doesNotContain("t1.type = 'Picture'");

    assertThat(generatedSql).contains("select t0.id, t0.name, t1.type, t1.id, t1.url, t1.note from mprofile t0 left join mmedia t1 on t1.id = t0.picture_id  where t0.name = ?");
    assertThat(generatedSql).contains("where t0.name = ?");
  }

}
