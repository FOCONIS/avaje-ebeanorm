package io.ebeaninternal.dbmigration.ddlgeneration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.ebeaninternal.dbmigration.ddlgeneration.platform.BaseDdlBuffer;
import io.ebeaninternal.dbmigration.model.MCompoundUniqueConstraint;
import io.ebeaninternal.dbmigration.model.MConfiguration;
import io.ebeaninternal.dbmigration.model.MIndex;
import io.ebeaninternal.dbmigration.model.MTable;
import io.ebeaninternal.dbmigration.model.ModelContainer;

/**
 * Write context holding the buffers for both apply and rollback DDL.
 */
public class DdlWrite {

  private final ModelContainer currentModel;

  private final DdlBuffer applyDropDependencies;

  private final DdlBuffer apply;

  private final DdlBuffer applyForeignKeys;

  private final DdlBuffer applyHistoryView;

  private final DdlBuffer applyHistoryTrigger;

  private final DdlBuffer dropAllForeignKeys;

  private final DdlBuffer dropAll;

  private final Set<Object> stashedIndices = new LinkedHashSet<>();

  private final DdlOptions options;

  /**
   * Create without any configuration or current model (no history support).
   */
  public DdlWrite() {
    this(new MConfiguration(), new ModelContainer(), new DdlOptions());
  }

  /**
   * Create with a configuration.
   */
  public DdlWrite(MConfiguration configuration, ModelContainer currentModel, DdlOptions options) {
    this.currentModel = currentModel;
    this.applyDropDependencies = new BaseDdlBuffer(configuration);
    this.apply = new BaseDdlBuffer(configuration);
    this.applyForeignKeys = new BaseDdlBuffer(configuration);
    this.applyHistoryView = new BaseDdlBuffer(configuration);
    this.applyHistoryTrigger = new BaseDdlBuffer(configuration);
    this.dropAllForeignKeys = new BaseDdlBuffer(configuration);
    this.dropAll = new BaseDdlBuffer(configuration);
    this.options = options;
  }

  /**
   * Return the DDL options.
   */
  public DdlOptions getOptions() {
    return options;
  }

  /**
   * Return the Table information from the current model.
   * <p>
   * This is typically required for the history support (used to determine the list of columns
   * included in the history when creating or recreating the associated trigger/stored procedure).
   * </p>
   */
  public MTable getTable(String tableName) {
    return currentModel.getTable(tableName);
  }

  /**
   * Returns all indices on the given table and column.
   * Index is only returned once!
   */
  public List<MIndex> findIndices(String tableName, String columnName) {
    List<MIndex> ret = new ArrayList<>();
    for (MIndex index : currentModel.getIndexes().values()) {
      if (index.getTableName().equals(tableName) && index.getColumns().contains(columnName)) {
        if (stashedIndices.add(index)) {
          ret.add(index);
        }
      }
    }
    return ret;
  }

  public List<MCompoundUniqueConstraint> findUniqueConstraints(String tableName, String columnName) {
    List<MCompoundUniqueConstraint> ret = new ArrayList<>();
    for (MCompoundUniqueConstraint constraint : currentModel.getTable(tableName).getUniqueConstraints()) {
      for (String column : constraint.getColumns()) {
        if (column.equals(columnName) && stashedIndices.add(constraint)) {
          ret.add(constraint);
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Return true if the apply buffers are all empty.
   */
  public boolean isApplyEmpty() {
    return apply.getBuffer().isEmpty()
      && applyForeignKeys.getBuffer().isEmpty()
      && applyHistoryView.getBuffer().isEmpty()
      && applyHistoryTrigger.getBuffer().isEmpty()
      && applyDropDependencies.getBuffer().isEmpty();
  }

  /**
   * Return the buffer that APPLY DDL is written to.
   */
  public DdlBuffer apply() {
    return apply;
  }

  /**
   * Return the buffer that executes early to drop dependencies like views etc.
   */
  public DdlBuffer applyDropDependencies() {
    return applyDropDependencies;
  }

  /**
   * Return the buffer that APPLY DDL is written to for foreign keys and their associated indexes.
   * <p>
   * Statements added to this buffer are executed after all the normal apply statements and
   * typically 'add foreign key' is added to this buffer.
   */
  public DdlBuffer applyForeignKeys() {
    return applyForeignKeys;
  }

  /**
   * Return the buffer that apply history-view DDL is written to.
   */
  public DdlBuffer applyHistoryView() {
    return applyHistoryView;
  }

  /**
   * Return the buffer that apply history-trigger DDL is written to.
   */
  public DdlBuffer applyHistoryTrigger() {
    return applyHistoryTrigger;
  }

  /**
   * Return the buffer used for the 'drop all DDL' for dropping foreign keys and associated indexes.
   */
  public DdlBuffer dropAllForeignKeys() {
    return dropAllForeignKeys;
  }

  /**
   * Return the buffer used for the 'drop all DDL' to drop tables, views and history triggers etc.
   */
  public DdlBuffer dropAll() {
    return dropAll;
  }

}
