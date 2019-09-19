package org.tests.compositekeys.db;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.ebean.Model;
import io.ebean.annotation.PrivateOwned;

@Entity
public class House extends Model implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private String id;

  @PrivateOwned
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "id.house")
  private List<Room> rooms;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Room> getRooms() {
    return rooms;
  }

  public void setRooms(List<Room> rooms) {
    this.rooms = rooms;
  }

}
