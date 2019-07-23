package org.tests.lazyforeignkeys;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")
public class EntityA extends ParentEntity {

  private String attr;

  public String getAttr() {
    return attr;
  }

  public void setAttr(String attr) {
    this.attr = attr;
  }
}
