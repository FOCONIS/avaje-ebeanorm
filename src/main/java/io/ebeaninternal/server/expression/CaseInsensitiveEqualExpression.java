package io.ebeaninternal.server.expression;

import io.ebean.ExpressionVisitor;
import io.ebeaninternal.api.SpiExpression;
import io.ebeaninternal.api.SpiExpressionRequest;
import io.ebeaninternal.server.el.ElPropertyValue;

import java.io.IOException;

class CaseInsensitiveEqualExpression extends AbstractValueExpression {

  private final boolean not;

  CaseInsensitiveEqualExpression(String propertyName, Object value, boolean not) {
    super(propertyName, value);
    this.not = not;
  }

  /**
   * Return the bind value taking into account named parameters.
   */
  private String val() {
    return strValue().toLowerCase();
  }

  @Override
  public void writeDocQuery(DocQueryContext context) throws IOException {
    if (not) {
      context.writeINotEqualTo(propName, val());
    } else {
      context.writeIEqualTo(propName, val());
    }
  }

  @Override
  public void addBindValues(SpiExpressionRequest request) {

    ElPropertyValue prop = getElProp(request);
    if (prop != null && prop.isDbEncrypted()) {
      // bind the key as well as the value
      String encryptKey = prop.getBeanProperty().getEncryptKey().getStringValue();
      request.addBindEncryptKey(encryptKey);
    }

    request.addBindValue(val());
  }

  @Override
  public void addSql(SpiExpressionRequest request) {

    String pname = propName;
    ElPropertyValue prop = getElProp(request);
    if (prop != null && prop.isDbEncrypted()) {
      pname = prop.getBeanProperty().getDecryptProperty(propName);
    }
    if (not) {
      request.append("lower(").append(pname).append(") != ?");
    } else {
      request.append("lower(").append(pname).append(") = ?");
    }
  }

  @Override
  public void queryPlanHash(StringBuilder builder) {
    if (not) {
      builder.append("Ine[").append(propName).append("]");
    } else {
      builder.append("Ieq[").append(propName).append("]");
    }
  }

  @Override
  public int queryBindHash() {
    return val().hashCode();
  }

  @Override
  public boolean isSameByBind(SpiExpression other) {
    CaseInsensitiveEqualExpression that = (CaseInsensitiveEqualExpression) other;
    return val().equals(that.val());
  }

  @Override
  public void visit(ExpressionVisitor visitor) {
    if (not) {
      visitor.ine(propName, strValue());
    } else {
      visitor.ieq(propName, strValue());
    }
  }
}
