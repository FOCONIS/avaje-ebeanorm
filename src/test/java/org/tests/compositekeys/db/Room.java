package org.tests.compositekeys.db;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import io.ebean.Model;

@Entity
public class Room extends Model implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private RoomPK id;

  public RoomPK getId() {
    return id;
  }
  
  public void setId(RoomPK id) {
    this.id = id;
  }
  
}
