package io.ebean.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Holds sets of additions and deletions from a 'owner' List Set or Map.
 * <p>
 * These sets of additions and deletions are used to support persisting
 * ManyToMany relationships. The additions becoming inserts into the
 * intersection table and the removals becoming deletes from the intersection
 * table.
 * </p>
 */
class ModifyHolder<E> implements Serializable {

  private static final long serialVersionUID = 2572572897923801083L;

  /**
   * Deletions list for manyToMany persistence.
   */
  private Set<E> modifyDeletions = new LinkedHashSet<>();

  /**
   * Additions list for manyToMany persistence.
   */
  private Set<E> modifyAdditions = new LinkedHashSet<>();

  void reset() {
    modifyDeletions = new LinkedHashSet<>();
    modifyAdditions = new LinkedHashSet<>();
  }

  /**
   * Used by BeanList.addAll() methods.
   */
  void modifyAdditionAll(Collection<? extends E> c) {
    if (c != null) {
      for (E e : c) {
        modifyAddition(e);
      }
    }
  }

  boolean undoDeletion(E bean) {
    return (bean != null) && modifyDeletions.remove(bean);
  }
  
  void modifyAddition(E bean) {
    if (bean != null) {
      // If it is to delete then just remove the deletion
      if (!undoDeletion(bean)) {
        // Insert
        modifyAdditions.add(bean);
      }
    }
  }

  boolean undoAddition(Object bean) {
    return (bean != null) && modifyAdditions.remove(bean);
  }
  
  @SuppressWarnings("unchecked")
  void modifyRemoval(Object bean) {
    if (bean != null) {
      // If it is to be added then just remove the addition
      if (!undoAddition(bean)) {
        modifyDeletions.add((E) bean);
      }
    }
  }

  Set<E> getModifyAdditions() {
    return modifyAdditions;
  }

  Set<E> getModifyRemovals() {
    return modifyDeletions;
  }

  /**
   * Return true if there additions or removals.
   */
  boolean hasModifications() {
    return !modifyDeletions.isEmpty() || !modifyAdditions.isEmpty();
  }
}
