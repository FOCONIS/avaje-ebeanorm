package io.ebeaninternal.server.expression;

import org.junit.Test;

import io.ebeaninternal.api.BindHash;
import io.ebeaninternal.server.querydefn.HashCodeBindHash;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class RawExpressionTest extends BaseExpressionTest {

  private RawExpression exp(String sql, Object... values) {
    return new RawExpression(sql, values);
  }

  @Test
  public void isSameByPlan_when_same() {
    same(exp("a", 10), exp("a", 10));
  }

  @Test
  public void isSameByPlan_when_diffBindValues() {
    same(exp("a", 10), exp("a", 20));
  }

  @Test
  public void isSameByPlan_when_diffSql() {
    different(exp("a", 10), exp("b", 10));
  }

  @Test
  public void isSameByBind_when_same() {
    assertThat(exp("a", 10).isSameByBind(exp("a", 10))).isTrue();
  }

  @Test
  public void isSameByBind_when_diffBindValues() {
    assertThat(exp("a", 10).isSameByBind(exp("a", 20))).isFalse();
  }

  @Test
  public void isSameByBind_when_moreBindValues() {
    assertThat(exp("a", 10).isSameByBind(exp("a", 10, 20))).isFalse();
  }

  @Test
  public void isSameByBind_when_lessBindValues() {
    assertThat(exp("a", 10, 20).isSameByBind(exp("a", 10))).isFalse();
  }

  @Test
  public void queryBindHash_when_sameBindValues() {
    assert_queryBindHash_isSame(exp("a", 10), exp("a", 10));
  }

  @Test
  public void queryBindHash_when_diffBindValues() {
    assert_queryBindHash_isDifferent(exp("a", 10), exp("a", 20));
  }

  @Test
  public void queryBindHash_when_diffBindValues2() {
    assert_queryBindHash_isDifferent(exp("a", 10), exp("a", 10, 11));
  }

  public void assert_queryBindHash_isDifferent(RawExpression exp0, RawExpression exp1) {
    assertThat(getHash(exp0)).isNotEqualTo(getHash(exp1));
  }

  public void assert_queryBindHash_isSame(RawExpression exp0, RawExpression exp1) {
    assertThat(getHash(exp0)).isEqualTo(getHash(exp1));
  }

  private int getHash(RawExpression query) {
    BindHash hash = new HashCodeBindHash();
    query.queryBindHash(hash);
    hash.finish();
    return hash.hashCode();
  }
}
