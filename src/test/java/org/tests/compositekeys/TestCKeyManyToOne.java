package org.tests.compositekeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tests.compositekeys.db.House;
import org.tests.compositekeys.db.Room;
import org.tests.compositekeys.db.RoomPK;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;

public class TestCKeyManyToOne extends BaseTestCase {

  @Test
  public void testManyToOneInCompositeKey() throws Exception {
    House house = new House();
    
    List<Room> rooms = new ArrayList<>();
    Room room = new Room();
    RoomPK roomId = new RoomPK();
    roomId.setRoomNo("123");
    room.setId(roomId);
    rooms.add(room);
    house.setRooms(rooms);
    house.save();
    
    house = Ebean.find(House.class).findOne();
    assertThat(house.getRooms()).hasSize(1);
  }
  
}
