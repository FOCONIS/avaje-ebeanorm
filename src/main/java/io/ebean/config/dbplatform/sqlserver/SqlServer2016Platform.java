package io.ebean.config.dbplatform.sqlserver;

import io.ebean.dbmigration.ddlgeneration.platform.SqlServer2016Ddl;

/**
 * SQL Server platform that uses SQL-HISTORY features
 */
public class SqlServer2016Platform extends SqlServerPlatform {

  public SqlServer2016Platform() {
    this.historySupport = new SqlServer2016HistorySupport();
    this.platformDdl = new SqlServer2016Ddl(this);

  }
}
