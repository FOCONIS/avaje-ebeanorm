package io.ebeaninternal.server.deploy;

import io.ebean.bean.BeanCollection;
import io.ebean.bean.EntityBean;
import io.ebeaninternal.server.text.json.SpiJsonWriter;

/**
 * Helper for element collection List.
 */
public class BeanSetHelpElement<T> extends BeanSetHelp<T> {

  BeanSetHelpElement(BeanPropertyAssocMany<T> many) {
    super(many);
  }

  @Override
  public void add(BeanCollection<?> collection, EntityBean bean, boolean withCheck) {
    Object elementValue = bean._ebean_getField(0);
    if (withCheck) {
      collection.internalAddWithCheck(elementValue);
    } else {
      collection.internalAdd(elementValue);
    }
  }

  @Override
  void jsonWriteElement(SpiJsonWriter ctx, Object element) {
    many.jsonWriteElementValue(ctx, element);
  }
}
