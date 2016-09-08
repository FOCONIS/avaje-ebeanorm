package com.avaje.ebean.config.dbplatform;

import com.avaje.ebean.config.PersistBatch;
import com.avaje.ebean.dbmigration.ddlgeneration.platform.MsSqlServerDdl;

import java.sql.Types;

/**
 * Microsoft SQL Server 2012 specific platform.
 * <p>
 * <ul>
 * <li>supportsGetGeneratedKeys = true</li>
 * <li>Uses LIMIT OFFSET clause</li>
 * <li>Uses [ & ] for quoted identifiers</li>
 * </ul>
 * </p>
 */
public class MsSqlServer2012Platform extends DatabasePlatform {

  public MsSqlServer2012Platform() {
    super();
    this.name = "mssqlserver2012";
    // effectively disable persistBatchOnCascade mode for SQL Server
    // due to lack of support for getGeneratedKeys in batch mode
    this.persistBatchOnCascade = PersistBatch.NONE;
    // enable DbViewHistorySupport - warning untested !
    this.historySupport = new MsSqlServer2012HistorySupport();
    this.idInExpandedForm = true;
    this.selectCountWithAlias = true;
    // uses ORDER BY OFFSET GET NEXT ROWS enhancement introduced in MS SQL Server 2012
    this.basicSqlLimiter = new BasicMsSqlLimiter();
    // FIXME: should be reworked to also use the built-in support in MS SQL Server 2012
    this.sqlLimiter = new MsSqlServer2005SqlLimiter();
    this.platformDdl = new MsSqlServerDdl(this);
    this.dbIdentity.setIdType(IdType.IDENTITY);
    this.dbIdentity.setSupportsGetGeneratedKeys(true);
    this.dbIdentity.setSupportsIdentity(true);

    this.openQuote = "[";
    this.closeQuote = "]";

    dbTypeMap.put(Types.BOOLEAN, new DbType("bit default 0"));

    dbTypeMap.put(Types.INTEGER, new DbType("integer", false));
    dbTypeMap.put(Types.BIGINT, new DbType("numeric", 19));
    dbTypeMap.put(Types.REAL, new DbType("float(16)"));
    dbTypeMap.put(Types.DOUBLE, new DbType("float(32)"));
    dbTypeMap.put(Types.TINYINT, new DbType("smallint"));
    dbTypeMap.put(Types.DECIMAL, new DbType("numeric", 28));

    dbTypeMap.put(Types.BLOB, new DbType("image"));
    dbTypeMap.put(Types.CLOB, new DbType("text"));
    dbTypeMap.put(Types.LONGVARBINARY, new DbType("image"));
    dbTypeMap.put(Types.LONGVARCHAR, new DbType("text"));

    dbTypeMap.put(Types.DATE, new DbType("date"));
    dbTypeMap.put(Types.TIME, new DbType("time"));
    dbTypeMap.put(Types.TIMESTAMP, new DbType("datetime2"));

  }
  
  @Override
  public String quoteValue(String value) {
    return value == null ? null: "'" + value.replace("'","''") + "'";
  }
  
  @Override
  public boolean needsIdentityInsert() {
    return true;
  }
}
