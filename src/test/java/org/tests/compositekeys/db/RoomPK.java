package org.tests.compositekeys.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

@Embeddable
public class RoomPK implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "id")
  private House house;

  @Size(max = 32)
  @Column(insertable = false, nullable = false)
  private String roomNo;

  public House getHouse() {
    return house;
  }

  public void setHouse(House house) {
    this.house = house;
  }

  public String getRoomNo() {
    return roomNo;
  }

  public void setRoomNo(String roomNo) {
    this.roomNo = roomNo;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((house == null) ? 0 : house.hashCode());
    result = prime * result + ((roomNo == null) ? 0 : roomNo.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RoomPK other = (RoomPK) obj;
    if (house == null) {
      if (other.house != null)
        return false;
    } else if (!house.equals(other.house))
      return false;
    if (roomNo == null) {
      if (other.roomNo != null)
        return false;
    } else if (!roomNo.equals(other.roomNo))
      return false;
    return true;
  }

  
}
