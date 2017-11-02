package io.ebeaninternal.server.transaction;

import io.ebean.config.CurrentTenantProvider;
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

  private final CurrentTenantProvider tenantProvider;

  TransactionFactoryTenant(TransactionManager manager, DataSourceSupplier dataSourceSupplier, CurrentTenantProvider tenantProvider) {
    super(manager);
    this.dataSourceSupplier = dataSourceSupplier;
    this.tenantProvider = tenantProvider;
  }

  @Override
  public SpiTransaction createQueryTransaction(Object tenantId) {
    return create(0,false, tenantId);
  }

  @Override
  public SpiTransaction createTransaction(int profileId, boolean explicit, int isolationLevel) {

    SpiTransaction t = create(profileId, explicit, null);
    return setIsolationLevel(t, explicit, isolationLevel);
  }

  private SpiTransaction create(int profileId, boolean explicit, Object tenantId) {
    Connection c = null;
    try {
      if (tenantId == null) {
        // tenantId not set (by lazy loading) so get current tenantId
        tenantId = tenantProvider.currentId();
      }
      c = dataSourceSupplier.getConnection(tenantId);
      SpiTransaction transaction = manager.createTransaction(profileId, explicit, c, counter.incrementAndGet());
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
