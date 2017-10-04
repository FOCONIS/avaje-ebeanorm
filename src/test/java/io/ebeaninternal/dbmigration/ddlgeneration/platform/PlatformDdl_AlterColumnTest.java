package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.Ebean;
import io.ebean.config.ServerConfig;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.sqlserver.SqlServerPlatform;
import io.ebean.config.dbplatform.mysql.MySqlPlatform;
import io.ebean.config.dbplatform.oracle.OraclePlatform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.server.core.PlatformDdlBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PlatformDdl_AlterColumnTest {

  PlatformDdl h2Ddl = PlatformDdlBuilder.create(new H2Platform());
  PlatformDdl pgDdl = PlatformDdlBuilder.create(new PostgresPlatform());
  PlatformDdl mysqlDdl = PlatformDdlBuilder.create(new MySqlPlatform());
  PlatformDdl oraDdl = PlatformDdlBuilder.create(new OraclePlatform());
  PlatformDdl sqlServerDdl = PlatformDdlBuilder.create(new SqlServerPlatform());

  {
    ServerConfig serverConfig = Ebean.getDefaultServer().getPluginApi().getServerConfig();
    sqlServerDdl.configure(serverConfig);
  }
  AlterColumn alterNotNull() {
    AlterColumn alterColumn = new AlterColumn();
    alterColumn.setTableName("mytab");
    alterColumn.setColumnName("acol");
    alterColumn.setCurrentType("varchar(5)");
    alterColumn.setNotnull(Boolean.TRUE);

    return alterColumn;
  }

  @Test
  public void convertArrayType_default() {
    assertThat(mysqlDdl.convertArrayType("varchar[](90)")).isEqualTo("varchar(90)");
    assertThat(mysqlDdl.convertArrayType("integer[](60)")).isEqualTo("varchar(60)");
    assertThat(mysqlDdl.convertArrayType("varchar[]")).isEqualTo("varchar(1000)");
    assertThat(mysqlDdl.convertArrayType("integer[]")).isEqualTo("varchar(1000)");
  }

  @Test
  public void convertArrayType_h2() {
    assertThat(h2Ddl.convertArrayType("varchar[](90)")).isEqualTo("array");
    assertThat(h2Ddl.convertArrayType("integer[](60)")).isEqualTo("array");
    assertThat(h2Ddl.convertArrayType("varchar[]")).isEqualTo("array");
    assertThat(h2Ddl.convertArrayType("integer[]")).isEqualTo("array");
  }

  @Test
  public void convertArrayType_postgres() {
    assertThat(pgDdl.convertArrayType("varchar[](90)")).isEqualTo("varchar[]");
    assertThat(pgDdl.convertArrayType("integer[](60)")).isEqualTo("integer[]");
    assertThat(pgDdl.convertArrayType("varchar[]")).isEqualTo("varchar[]");
    assertThat(pgDdl.convertArrayType("integer[]")).isEqualTo("integer[]");
  }

  @Test
  public void testAlterColumnBaseAttributes() throws Exception {

    AlterColumn alterColumn = alterNotNull();
    assertNull(h2Ddl.alterColumnBaseAttributes(alterColumn));
    assertNull(pgDdl.alterColumnBaseAttributes(alterColumn));
    assertNull(oraDdl.alterColumnBaseAttributes(alterColumn));

    String sql = mysqlDdl.alterColumnBaseAttributes(alterColumn);
    assertEquals("alter table mytab modify acol varchar(5) not null", sql);

    sql = sqlServerDdl.alterColumnBaseAttributes(alterColumn);
    assertEquals("alter table mytab alter column acol varchar(5) not null", sql);

    alterColumn.setNotnull(Boolean.FALSE);
    sql = mysqlDdl.alterColumnBaseAttributes(alterColumn);
    assertEquals("alter table mytab modify acol varchar(5)", sql);

    alterColumn.setNotnull(null);
    alterColumn.setType("varchar(100)");

    sql = mysqlDdl.alterColumnBaseAttributes(alterColumn);
    assertEquals("alter table mytab modify acol varchar(100)", sql);

    alterColumn.setCurrentNotnull(Boolean.TRUE);
    sql = mysqlDdl.alterColumnBaseAttributes(alterColumn);
    assertEquals("alter table mytab modify acol varchar(100) not null", sql);
  }

  @Test
  public void testAlterColumnType() throws Exception {

    String sql = h2Ddl.alterColumnType("mytab", "acol", "varchar(20)");
    assertEquals("alter table mytab alter column acol varchar(20)", sql);

    sql = pgDdl.alterColumnType("mytab", "acol", "varchar(20)");
    assertEquals("alter table mytab alter column acol type varchar(20)", sql);

    sql = oraDdl.alterColumnType("mytab", "acol", "varchar(20)");
    assertEquals("alter table mytab modify acol varchar2(20)", sql);

    sql = mysqlDdl.alterColumnType("mytab", "acol", "varchar(20)");
    assertNull(sql);

    sql = sqlServerDdl.alterColumnType("mytab", "acol", "varchar(20)");
    assertNull(sql);
  }

  @Test
  public void testAlterColumnNotnull() throws Exception {

    String sql = h2Ddl.alterColumnNotnull("mytab", "acol", true);
    assertEquals("alter table mytab alter column acol set not null", sql);

    sql = pgDdl.alterColumnNotnull("mytab", "acol", true);
    assertEquals("alter table mytab alter column acol set not null", sql);

    sql = oraDdl.alterColumnNotnull("mytab", "acol", true);
    assertEquals("alter table mytab modify acol not null", sql);

    sql = mysqlDdl.alterColumnNotnull("mytab", "acol", true);
    assertNull(sql);

    sql = sqlServerDdl.alterColumnNotnull("mytab", "acol", true);
    assertNull(sql);
  }

  @Test
  public void testAlterColumnNull() throws Exception {

    String sql = h2Ddl.alterColumnNotnull("mytab", "acol", false);
    assertEquals("alter table mytab alter column acol set null", sql);

    sql = pgDdl.alterColumnNotnull("mytab", "acol", false);
    assertEquals("alter table mytab alter column acol drop not null", sql);

    sql = oraDdl.alterColumnNotnull("mytab", "acol", false);
    assertEquals("alter table mytab modify acol null", sql);

    sql = mysqlDdl.alterColumnNotnull("mytab", "acol", false);
    assertNull(sql);

    sql = sqlServerDdl.alterColumnNotnull("mytab", "acol", false);
    assertNull(sql);
  }

  @Test
  public void testAlterColumnDefaultValue() throws Exception {

    String sql = h2Ddl.alterColumnDefaultValue("mytab", "acol", "'hi'");
    assertEquals("alter table mytab alter column acol set default 'hi'", sql);

    sql = pgDdl.alterColumnDefaultValue("mytab", "acol", "'hi'");
    assertEquals("alter table mytab alter column acol set default 'hi'", sql);

    sql = oraDdl.alterColumnDefaultValue("mytab", "acol", "'hi'");
    assertEquals("alter table mytab modify acol default 'hi'", sql);

    sql = mysqlDdl.alterColumnDefaultValue("mytab", "acol", "'hi'");
    assertEquals("alter table mytab alter acol set default 'hi'", sql);

    sql = sqlServerDdl.alterColumnDefaultValue("mytab", "acol", "'hi'");
    assertEquals("alter table mytab add default 'hi' for acol", sql);
  }

  @Test
  public void testAlterColumnDropDefault() throws Exception {

    String sql = h2Ddl.alterColumnDefaultValue("mytab", "acol", "DROP DEFAULT");
    assertEquals("alter table mytab alter column acol drop default", sql);

    sql = pgDdl.alterColumnDefaultValue("mytab", "acol", "DROP DEFAULT");
    assertEquals("alter table mytab alter column acol drop default", sql);

    sql = oraDdl.alterColumnDefaultValue("mytab", "acol", "DROP DEFAULT");
    // seems that ther is no drop default on oracle
    assertEquals("alter table mytab modify acol default null", sql); 

    sql = mysqlDdl.alterColumnDefaultValue("mytab", "acol", "DROP DEFAULT");
    assertEquals("alter table mytab alter acol drop default", sql);

    sql = sqlServerDdl.alterColumnDefaultValue("mytab", "acol", "DROP DEFAULT");
    assertThat(sql).startsWith("delimiter $$").endsWith("$$");
  }

}
