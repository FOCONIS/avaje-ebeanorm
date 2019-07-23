package org.tests.lazyforeignkeys;

import io.ebean.annotation.Formula;
import io.ebean.annotation.Platform;
import io.ebean.annotation.SoftDelete;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import javax.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="kind")
@Table(name = "parent_entity")
public abstract class ParentEntity {

  @Id
  @Size(max = 16)
  private String id;

  @SoftDelete
  @Formula(select = "${ta}.id is null")
  @Formula(select = "CASE WHEN ${ta}.id is null THEN 1 ELSE 0 END", platforms = Platform.SQLSERVER17)
  // evaluates to true in a left join if bean has been deleted.
  private boolean deleted;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isDeleted() {
    return deleted;
  }
}
