package io.ebean.config.dbplatform.h2;

import io.ebean.BackgroundExecutor;
import io.ebean.TenantContext;
import io.ebean.config.TenantDataSourceProvider;
import io.ebean.config.dbplatform.SequenceIdGenerator;

/**
 * H2 specific sequence Id Generator.
 */
public class H2SequenceIdGenerator extends SequenceIdGenerator {

  private final String baseSql;
  private final String unionBaseSql;

  /**
   * Construct given a dataSource and sql to return the next sequence value.
   */
  public H2SequenceIdGenerator(BackgroundExecutor be, TenantDataSourceProvider ds, String seqName, int batchSize, boolean perTenant, TenantContext tenantContext) {
    super(be, ds, seqName, batchSize, perTenant, tenantContext);
    this.baseSql = "select " + seqName + ".nextval";
    this.unionBaseSql = " union " + baseSql;
  }

  @Override
  public String getSql(int batchSize) {

    StringBuilder sb = new StringBuilder();
    sb.append(baseSql);
    for (int i = 1; i < batchSize; i++) {
      sb.append(unionBaseSql);
    }
    return sb.toString();
  }
}
