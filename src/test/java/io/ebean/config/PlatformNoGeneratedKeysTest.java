package io.ebean.config;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.Transaction;
import io.ebean.annotation.Platform;
import io.ebean.config.dbplatform.DbIdentity;
import io.ebean.config.dbplatform.IdType;
import io.ebean.config.dbplatform.h2.H2Platform;
import org.junit.Test;
import org.tests.model.basic.EBasicVer;
import org.tests.model.draftable.BasicDraftableBean;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class PlatformNoGeneratedKeysTest {

  static EbeanServer server = testH2Server();

  @Test
  public void insertBatch_expect_noIdValuesFetched() {

    EBasicVer b0 = new EBasicVer("a");
    EBasicVer b1 = new EBasicVer("b");
    EBasicVer b2 = new EBasicVer("c");

    try (Transaction transaction = server.beginTransaction()) {
      transaction.setBatchMode(true);

      server.save(b0);
      server.save(b1);
      server.save(b2);

      transaction.commit();
    }

    assertThat(b0.getId()).isNull();
    assertThat(b1.getId()).isNull();
    assertThat(b2.getId()).isNull();

  }

  @Test
  public void insertNoBatch_expect_selectIdentity() {

    EBasicVer b0 = new EBasicVer("one");
    server.save(b0);

    assertThat(b0.getId()).isNotNull();


    BasicDraftableBean d0 = new BasicDraftableBean("done");
    server.save(d0);

    assertThat(d0.getId()).isNotNull();

    server.publish(BasicDraftableBean.class, d0.getId());

    BasicDraftableBean one = server.find(BasicDraftableBean.class, d0.getId());

    assertThat(one.getName()).isEqualTo("done");
    assertThat(one.isDraft()).isFalse();
  }

  private static EbeanServer testH2Server() {

    ServerConfig config = new ServerConfig();
    config.setName("h2_noGeneratedKeys");

    OtherH2Platform platform = new OtherH2Platform();
    DbIdentity dbIdentity = platform.getDbIdentity();
    dbIdentity.setIdType(IdType.IDENTITY);
    dbIdentity.setSupportsIdentity(true);
    dbIdentity.setSupportsGetGeneratedKeys(false);
    dbIdentity.setSupportsSequence(false);
    dbIdentity.setSelectLastInsertedIdTemplate("select identity() --{table}");

    config.setDatabasePlatform(platform);
    config.getDataSourceConfig().setUsername("sa");
    config.getDataSourceConfig().setPassword("");
    config.getDataSourceConfig().setUrl("jdbc:h2:mem:withPCQuery;");
    config.getDataSourceConfig().setDriver("org.h2.Driver");

    config.setDisableL2Cache(true);
    config.setDefaultServer(false);
    config.setRegister(false);
    config.setDdlGenerate(true);
    config.setDdlRun(true);
    config.getClasses().add(EBasicVer.class);
    config.getClasses().add(BasicDraftableBean.class);


    return EbeanServerFactory.create(config);
  }

  static class OtherH2Platform extends H2Platform {

    OtherH2Platform() {
      super();
      this.platform = Platform.GENERIC;
    }
  }
}
