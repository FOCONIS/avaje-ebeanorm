package io.ebeaninternal.server.transaction;

import io.ebean.TenantContext;
import io.ebeaninternal.api.SpiTransaction;
import io.ebeaninternal.util.JdbcClose;

import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction factory used with multi-tenancy.
 */
class TransactionFactoryTenant extends TransactionFactory {

  private final DataSourceSupplier dataSourceSupplier;

  private final TenantContext tenantContext;

  TransactionFactoryTenant(TransactionManager manager, DataSourceSupplier dataSourceSupplier, TenantContext tenantContext) {
    super(manager);
    this.dataSourceSupplier = dataSourceSupplier;
    this.tenantContext = tenantContext;
  }

  @Override
  public SpiTransaction createQueryTransaction(Object tenantId) {
    return create(false, tenantId);
  }

  @Override
  public SpiTransaction createTransaction(boolean explicit, int isolationLevel) {

    SpiTransaction t = create(explicit, null);
    return setIsolationLevel(t, explicit, isolationLevel);
  }

  private SpiTransaction create(boolean explicit, Object tenantId) {
    Connection c = null;
    try {
      if (tenantId == null) {
        // tenantId not set (by lazy loading) so get current tenantId
        tenantId = tenantContext.getTenantId();
      }
      c = dataSourceSupplier.getConnection(tenantId);
      SpiTransaction transaction = manager.createTransaction(explicit, c, counter.incrementAndGet());
      transaction.setTenantId(tenantId);
      return transaction;

    } catch (PersistenceException ex) {
      JdbcClose.close(c);
      throw ex;

    } catch (SQLException ex) {
      throw new PersistenceException(ex);
    }
  }

}
