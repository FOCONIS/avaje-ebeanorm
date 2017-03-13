package io.ebeaninternal.api;

import io.ebean.PersistBatch;
import io.ebean.TransactionCallback;
import io.ebean.annotation.DocStoreMode;
import io.ebean.bean.PersistenceContext;
import io.ebean.event.changelog.BeanChange;
import io.ebean.event.changelog.ChangeSet;
import io.ebeaninternal.server.core.PersistDeferredRelationship;
import io.ebeaninternal.server.core.PersistRequest;
import io.ebeaninternal.server.core.PersistRequestBean;
import io.ebeaninternal.server.persist.BatchControl;
import io.ebeanservice.docstore.api.DocStoreTransaction;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wrapper of a ScopeTrans request and it's underlying transaction.
 */
public class ScopedTransaction implements SpiTransaction {

  private final ScopeTrans scopeTrans;

  private final SpiTransaction transaction;

  private boolean committed;

  public ScopedTransaction(ScopeTrans scopeTrans) {
    this.scopeTrans = scopeTrans;
    this.transaction = scopeTrans.getTransaction();
  }

  @Override
  public PersistenceException translate(String message, SQLException cause) {
    return transaction.translate(message, cause);
  }

  @Override
  public void commitAndContinue() {
    transaction.commitAndContinue();
  }

  @Override
  public void commit() {
    scopeTrans.commitTransaction();
    committed = true;
  }

  @Override
  public void rollback() throws PersistenceException {
    scopeTrans.rollback(null);
  }

  @Override
  public void rollback(Throwable e) throws PersistenceException {
    scopeTrans.rollback(e);
  }

  @Override
  public void rollbackIfActive() {
    transaction.rollbackIfActive();
  }

  @Override
  public void setRollbackOnly() {
    scopeTrans.setRollbackOnly();
  }

  @Override
  public boolean isRollbackOnly() {
    return transaction.isRollbackOnly();
  }

  @Override
  public void end() throws PersistenceException {
    try {
      if (!committed) {
        scopeTrans.rollback(null);
      }
    } finally {
      scopeTrans.restoreSuspended();
    }
  }

  @Override
  public DocStoreTransaction getDocStoreTransaction() {
    return transaction.getDocStoreTransaction();
  }

  @Override
  public DocStoreMode getDocStoreMode() {
    return transaction.getDocStoreMode();
  }

  @Override
  public void setDocStoreMode(DocStoreMode mode) {
    transaction.setDocStoreMode(mode);
  }

  @Override
  public int getDocStoreBatchSize() {
    return transaction.getDocStoreBatchSize();
  }

  @Override
  public void setDocStoreBatchSize(int batchSize) {
    transaction.setDocStoreBatchSize(batchSize);
  }

  @Override
  public String getLogPrefix() {
    return transaction.getLogPrefix();
  }

  @Override
  public boolean isLogSql() {
    return transaction.isLogSql();
  }

  @Override
  public boolean isLogSummary() {
    return transaction.isLogSummary();
  }

  @Override
  public void logSql(String msg) {
    transaction.logSql(msg);
  }

  @Override
  public void logSummary(String msg) {
    transaction.logSummary(msg);
  }

  @Override
  public void setSkipCache(boolean skipCache) {
    transaction.setSkipCache(skipCache);
  }

  @Override
  public boolean isSkipCache() {
    return transaction.isSkipCache();
  }

  @Override
  public void addBeanChange(BeanChange beanChange) {
    transaction.addBeanChange(beanChange);
  }

  @Override
  public void sendChangeLog(ChangeSet changes) {
    transaction.sendChangeLog(changes);
  }

  @Override
  public void registerDeferred(PersistDeferredRelationship derived) {
    transaction.registerDeferred(derived);
  }

  @Override
  public void registerDeleteBean(Integer hash) {
    transaction.registerDeleteBean(hash);
  }

  @Override
  public void unregisterDeleteBean(Integer hash) {
    transaction.unregisterDeleteBean(hash);
  }

  @Override
  public boolean isRegisteredDeleteBean(Integer hash) {
    return transaction.isRegisteredDeleteBean(hash);
  }

  @Override
  public void unregisterBean(Object bean) {
    transaction.unregisterBean(bean);
  }

  @Override
  public boolean isRegisteredBean(Object bean) {
    return transaction.isRegisteredBean(bean);
  }

  @Override
  public String getId() {
    return transaction.getId();
  }

  @Override
  public void register(TransactionCallback callback) {
    transaction.register(callback);
  }

  @Override
  public boolean isReadOnly() {
    return transaction.isReadOnly();
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    transaction.setReadOnly(readOnly);
  }

  @Override
  public boolean isActive() {
    return transaction.isActive();
  }

  @Override
  public void setPersistCascade(boolean persistCascade) {
    transaction.setPersistCascade(persistCascade);
  }

  @Override
  public void setUpdateAllLoadedProperties(boolean updateAllLoaded) {
    transaction.setUpdateAllLoadedProperties(updateAllLoaded);
  }

  @Override
  public Boolean isUpdateAllLoadedProperties() {
    return transaction.isUpdateAllLoadedProperties();
  }

  @Override
  public void setBatchMode(boolean useBatch) {
    transaction.setBatchMode(useBatch);
  }

  @Override
  public void setBatch(PersistBatch persistBatchMode) {
    transaction.setBatch(persistBatchMode);
  }

  @Override
  public PersistBatch getBatch() {
    return transaction.getBatch();
  }

  @Override
  public void setBatchOnCascade(PersistBatch batchOnCascadeMode) {
    transaction.setBatchOnCascade(batchOnCascadeMode);
  }

  @Override
  public PersistBatch getBatchOnCascade() {
    return transaction.getBatchOnCascade();
  }

  @Override
  public void setBatchSize(int batchSize) {
    transaction.setBatchSize(batchSize);
  }

  @Override
  public int getBatchSize() {
    return transaction.getBatchSize();
  }

  @Override
  public void setBatchGetGeneratedKeys(boolean getGeneratedKeys) {
    transaction.setBatchGetGeneratedKeys(getGeneratedKeys);
  }

  @Override
  public Boolean getBatchGetGeneratedKeys() {
    return transaction.getBatchGetGeneratedKeys();
  }

  @Override
  public void setBatchFlushOnMixed(boolean batchFlushOnMixed) {
    transaction.setBatchFlushOnMixed(batchFlushOnMixed);
  }

  @Override
  public void setBatchFlushOnQuery(boolean batchFlushOnQuery) {
    transaction.setBatchFlushOnQuery(batchFlushOnQuery);
  }

  @Override
  public boolean isBatchFlushOnQuery() {
    return transaction.isBatchFlushOnQuery();
  }

  @Override
  public void flushBatch() throws PersistenceException {
    transaction.flushBatch();
  }

  @Override
  public Connection getConnection() {
    return transaction.getConnection();
  }

  @Override
  public void addModification(String tableName, boolean inserts, boolean updates, boolean deletes) {
    transaction.addModification(tableName, inserts, updates, deletes);
  }

  @Override
  public void putUserObject(String name, Object value) {
    transaction.putUserObject(name, value);
  }

  @Override
  public Object getUserObject(String name) {
    return transaction.getUserObject(name);
  }

  @Override
  public void depth(int diff) {
    transaction.depth();
  }

  @Override
  public int depth() {
    return transaction.depth();
  }

  @Override
  public boolean isExplicit() {
    return transaction.isExplicit();
  }

  @Override
  public TransactionEvent getEvent() {
    return transaction.getEvent();
  }

  @Override
  public boolean isPersistCascade() {
    return transaction.isPersistCascade();
  }

  @Override
  public boolean isBatchThisRequest(PersistRequest.Type type) {
    return transaction.isBatchThisRequest(type);
  }

  @Override
  public BatchControl getBatchControl() {
    return transaction.getBatchControl();
  }

  @Override
  public void setBatchControl(BatchControl control) {
    transaction.setBatchControl(control);
  }

  @Override
  public PersistenceContext getPersistenceContext() {
    return transaction.getPersistenceContext();
  }

  @Override
  public void setPersistenceContext(PersistenceContext context) {
    transaction.setPersistenceContext(context);
  }

  @Override
  public Connection getInternalConnection() {
    return transaction.getInternalConnection();
  }

  @Override
  public boolean isSaveAssocManyIntersection(String intersectionTable, String beanName) {
    return transaction.isSaveAssocManyIntersection(intersectionTable, beanName);
  }

  @Override
  public boolean checkBatchEscalationOnCascade(PersistRequestBean<?> request) {
    return transaction.checkBatchEscalationOnCascade(request);
  }

  @Override
  public void flushBatchOnCascade() {
    transaction.flushBatchOnCascade();
  }

  @Override
  public void flushBatchOnRollback() {
    transaction.flushBatchOnRollback();
  }

  @Override
  public void markNotQueryOnly() {
    transaction.markNotQueryOnly();
  }

  @Override
  public void checkBatchEscalationOnCollection() {
    transaction.checkBatchEscalationOnCollection();
  }

  @Override
  public void flushBatchOnCollection() {
    transaction.flushBatchOnCollection();
  }

  @Override
  public void close() throws IOException {
    transaction.close();
  }
}
