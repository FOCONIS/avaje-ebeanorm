package org.tests.query.cache;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.ExpressionList;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDistinct extends BaseTestCase {
  /**
   * The call of {@link #positionsQuery} {@link ExpressionList#findList()} causes the following use of {@link #costsQuery(UUID...)} to not
   * use distinct and return the wrong number of results.
   */
  @Test
  public void testMissingUnique() {
    Acl acl = new Acl("1");
    Ebean.save(acl);
    Acl acl2 = new Acl("2");
    Ebean.save(acl2);
    Contract contract = new Contract();
    AclContainerRelation rel1 = new AclContainerRelation();
    rel1.setAclEntry(acl);
    rel1.setContainer(contract);
    AclContainerRelation rel2 = new AclContainerRelation();
    rel2.setAclEntry(acl2);
    rel2.setContainer(contract);
    contract.getAclEntries().add(rel1);
    contract.getAclEntries().add(rel2);
    Position pos = new Position();
    pos.setContract(contract);
    contract.getPositions().add(pos);
    Ebean.save(contract);
    ContractCosts cost = new ContractCosts();
    cost.setPosition(pos);
    Ebean.save(cost);

    //costsQuery(acl.getId(), acl2.getId()).findCount();

    // the between causes the error
    List<Position> positions = positionsQuery(acl.getId()).findList();
    // the between causes the error

    System.out.println("The error, query without distinct:");
    List<ContractCosts> costs = costsQuery(acl.getId(), acl2.getId()).findList();
    assertThat(costs).hasSize(1);
  }
  
  /**
   * Generated query with a distinct and a sort on a column calculated with a {@link Formula} runs successfully and doesn't throw {@link PersistenceException}.
   */
  // on Sql server test throws: javax.persistence.PersistenceException: Query threw SQLException:ORDER BY items must appear in the select list if SELECT DISTINCT is specified
  @Test
  public void testOrderByFormula() {
    List<Contract> contracts = Ebean.find(Contract.class).where().in("aclEntries.aclEntry.id", "xx").orderBy().asc("furmulated").findList();
  }

  public ExpressionList<Position> positionsQuery(final Long aclId) {
    return Ebean.find(Position.class).where().eq("contract.aclEntries.aclEntry.id", aclId);
  }

  public ExpressionList<ContractCosts> costsQuery(final Long... aclId) {
    return Ebean.find(ContractCosts.class).where().in("position.contract.aclEntries.aclEntry.id", aclId);
  }
}
