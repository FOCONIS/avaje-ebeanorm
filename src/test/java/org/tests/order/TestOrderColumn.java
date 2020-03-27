package org.tests.order;

import io.ebean.Ebean;
import io.ebean.TransactionalTestCase;
import org.ebeantest.LoggedSqlCollector;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOrderColumn extends TransactionalTestCase {

  @Test
  public void testOrderColumnInheritance() {
    final OrderMaster master = new OrderMaster();

    for (int i = 0; i < 5; i++) {
      final OrderReferencedChild child = new OrderReferencedChild("p" + i);
      child.setChildName("c" + i);

      master.getChildren().add(child);
    }

    Ebean.save(master);

    final OrderMaster result = Ebean.find(OrderMaster.class).findOne();

    assertThat(result.getChildren()).hasSize(5);
    assertThat(result.getChildren()).extracting(OrderReferencedChild::getName).containsExactly("p0", "p1", "p2", "p3", "p4");
    assertThat(result.getChildren()).extracting(OrderReferencedChild::getChildName).containsExactly("c0", "c1", "c2", "c3", "c4");
  }

  @Test
  public void testModifyTree() {
    final OrderMaster master = new OrderMaster();

    for (int i = 0; i < 5; i++) {
      final OrderReferencedChild child = new OrderReferencedChild("p" + i);
      child.setChildName("c" + i);

      for (int j = 0; j < 3; j++) {
        final OrderToy toy = new OrderToy("t" + i + j);
        child.getToys().add(toy);
      }

      master.getChildren().add(child);
    }

    Ebean.save(master);

    final OrderMaster result = Ebean.find(OrderMaster.class).findOne();

    final List<OrderReferencedChild> children = result.getChildren();
    assertThat(children).hasSize(5);

    for (int i = 0; i < 5; i++) {
      final List<OrderToy> toys = children.get(i).getToys();

      for (int j = 0; j < 3; j++) {
        final OrderToy toy = toys.get(j);
        assertThat(toy.getTitle()).isEqualTo("t" + i + j);
      }
    }

    // modify two toys
    children.get(1).getToys().get(0).setTitle("tt10");
    children.get(3).getToys().get(2).setTitle("tt32");

    LoggedSqlCollector.start();
    Ebean.save(result);
    final List<String> sql = LoggedSqlCollector.current();
    assertThat(sql).hasSize(3);

    assertThat(sql.get(0)).contains("update order_toy set title=?, sort_order=? where id=?");
    assertThat(sql.get(1)).contains("bind(tt10");
    assertThat(sql.get(2)).contains("bind(tt32");
  }

  @Test
  public void testRemoveElement() {
    final OrderMaster master = new OrderMaster();

    for (int i = 0; i < 5; i++) {
      final OrderReferencedChild child = new OrderReferencedChild("p" + i);
      child.setChildName("c" + i);

      for (int j = 0; j < 3; j++) {
        final OrderToy toy = new OrderToy("t" + i + j);
        child.getToys().add(toy);
      }

      master.getChildren().add(child);
    }

    Ebean.save(master);

    final OrderMaster result = Ebean.find(OrderMaster.class).findOne();

    final List<OrderReferencedChild> children = result.getChildren();
    assertThat(children).hasSize(5);

    final OrderReferencedChild child = children.get(0);

    final OrderToy removedToy = child.getToys().remove(1);

    LoggedSqlCollector.start();
    Ebean.save(result);
    final List<String> sql = LoggedSqlCollector.current();

    assertThat(Ebean.find(OrderToy.class).where().eq("id", removedToy.getId()).findCount()).isEqualTo(0);

    assertThat(sql).hasSize(4);
    assertThat(sql.get(0)).contains("delete from order_toy where id=?");
    assertThat(sql.get(2)).contains("update order_toy set sort_order=? where id=?");
    assertThat(sql.get(3)).contains("bind(2,");
  }

}
