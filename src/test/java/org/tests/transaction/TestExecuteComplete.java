package org.tests.transaction;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.DataIntegrityException;
import io.ebean.Transaction;
import io.ebean.TxScope;
import io.ebean.annotation.ForPlatform;
import io.ebean.annotation.PersistBatch;
import io.ebean.annotation.Platform;
import io.ebean.annotation.Transactional;
import io.ebeaninternal.api.ScopedTransaction;
import io.ebeaninternal.api.SpiTransaction;
import io.ebeaninternal.server.transaction.DefaultTransactionThreadLocal;

import org.junit.Ignore;
import org.junit.Test;
import org.tests.model.basic.Customer;
import org.tests.model.basic.Order;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

public class TestExecuteComplete extends BaseTestCase {


  @ForPlatform(Platform.H2)
  @Test
  public void execute_when_errorOnCommit_threadLocalIsCleared() {

    try {
      DB.execute(TxScope.required().setBatch(PersistBatch.ALL), () -> {

        Customer customer = DB.getReference(Customer.class, 42424242L);
        Order order = new Order();
        order.setCustomer(customer);

        DB.save(order);
      });
      fail();
    } catch (DataIntegrityException e) {
      // assert the thread local has been cleaned up
      SpiTransaction txn = DefaultTransactionThreadLocal.get("h2");
      assertThat(txn).isNull();
    }
  }

  @ForPlatform(Platform.H2)
  @Test
  public void nestedExecute_when_errorOnCommit_threadLocalIsCleared() {

    try {
      DB.execute(TxScope.required().setBatch(PersistBatch.ALL), () ->
      DB.execute(() -> {

          Customer customer = DB.getReference(Customer.class, 42424242L);
          Order order = new Order();
          order.setCustomer(customer);

          DB.save(order);
        }));
      fail();
    } catch (DataIntegrityException e) {
      // assert the thread local has been cleaned up
      SpiTransaction txn = DefaultTransactionThreadLocal.get("h2");
      assertThat(txn).isNull();
    }
  }

  @ForPlatform(Platform.H2)
  @Test
  public void transactional_errorOnCommit_expect_threadScopeCleanup() {

    try {
      errorOnCommit();
      fail();
    } catch (DataIntegrityException e) {
      SpiTransaction txn = DefaultTransactionThreadLocal.get("h2");
      assertThat(txn).isNull();
    }
  }

  @Transactional(batchSize = 10)
  private void errorOnCommit() {
    Customer customer = DB.getReference(Customer.class, 42424242L);
    Order order = new Order();
    order.setCustomer(customer);

    DB.save(order);
  }

  @ForPlatform(Platform.H2)
  @Test
  public void normal_expect_threadScopeCleanup() {

    Transaction txn1 = server().beginTransaction();
    try {
      txn1.commit();
    } finally {
      txn1.end();
    }

    SpiTransaction txn2 = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn2).isNull();
  }

  @ForPlatform(Platform.H2)
  @Test
  public void missingEnd_expect_threadScopeCleanup() {

    Transaction txn1 = server().beginTransaction();
    try {
      txn1.commit();
    } finally {
      // accidentally omit end()
      //txn1.end();
    }

    SpiTransaction txn2 = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn2).isNull();
  }

  @ForPlatform(Platform.H2)
  @Test
  public void missingEnd_withRollbackOnly_expect_threadScopeCleanup() {

    Transaction txn1 = server().beginTransaction();
    try {
      txn1.rollback();
    } finally {
      // accidentally omit end()
      //txn1.end();
    }

    SpiTransaction txn2 = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn2).isNull();
  }

  @ForPlatform(Platform.H2)
  @Test
  public void implicit_query_expect_threadScopeCleanup() {

    DB.find(Customer.class).findList();

    SpiTransaction txn = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn).isNull();
  }

  @ForPlatform(Platform.H2)
  @Test
  public void implicit_save_expect_threadScopeCleanup() {

    Customer cust = new Customer();
    cust.setName("Roland");
    DB.save(cust);

    SpiTransaction txn = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn).isNull();
  }

  @ForPlatform(Platform.H2)
  @Test
  public void no_transaction_expect_threadScopeCleanup() {

    try (Transaction txn = DB.beginTransaction(TxScope.notSupported())) {
      SpiTransaction txn2 = DefaultTransactionThreadLocal.get("h2");
      // The NoTransaction placeholder can normally only occur inside
      // a scopedTrans. (Class is package private, so check
      assertThat(txn2.toString()).contains("NoTransaction");
      assertThat(txn2).isInstanceOf(ScopedTransaction.class);
    }

    SpiTransaction txn = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn).isNull();
  }

  @ForPlatform(Platform.H2)
  @Test
  @Ignore("Test is ignored as it may consume too much time")
  public void transactional_errorOnOOM_expect_threadScopeCleanup() {

    assertThatThrownBy(this::errorOnOOM).isInstanceOf(OutOfMemoryError.class);

    Transaction txn = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn).isNull();
  }

  @Transactional(batchSize = 10)
  private void errorOnOOM() {
    List<Object> lst = new ArrayList<>();
    while (true) {
      lst.add(new int[10000]);
    }
  }


  @ForPlatform(Platform.H2)
  @Test
  @Ignore("disabled by default")
  // If this test is enabled, and tests are executed with enabeld memory-leak detection,
  // (mvn verify -Debean.detectTransactionLeaks=true) a stacktrace should be printed on shutdown
  // identifying this code place
  public void transactional_without_end() {

    Transaction txn1 = server().beginTransaction();

    Transaction txn = DefaultTransactionThreadLocal.get("h2");
    assertThat(txn).isNotNull();
    throw new RuntimeException();
  }


}
