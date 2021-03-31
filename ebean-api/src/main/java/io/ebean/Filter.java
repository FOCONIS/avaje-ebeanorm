package io.ebean;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Provides support for filtering and sorting lists of entities without going
 * back to the database.
 * <p>
 * That is, it uses local in-memory sorting and filtering of a list of entity
 * beans. It is not used in a Database query or invoke a Database query.
 * </p>
 * <p>
 * You can optionally specify a sortByClause and if so, the sort will always
 * execute prior to the filter expressions. You can specify any number of filter
 * expressions and they are effectively joined by logical "AND".
 * </p>
 * <p>
 * The result of the filter method will leave the original list unmodified and
 * return a new List instance.
 * </p>
 * <p>
 * <pre>{@code
 *
 * // get a list of entities (query execution statistics in this case)
 *
 * List<MetaQueryStatistic> list =
 *     DB.find(MetaQueryStatistic.class).findList();
 *
 * long nowMinus24Hrs = System.currentTimeMillis() - 24 * (1000 * 60 * 60);
 *
 * // sort and filter the list returning a filtered list...
 *
 * List<MetaQueryStatistic> filteredList =
 *     DB.filter(MetaQueryStatistic.class)
 *         .sort("avgTimeMicros desc")
 *         .gt("executionCount", 0)
 *         .gt("lastQueryTime", nowMinus24Hrs)
 *         .eq("autoTuned", true)
 *         .maxRows(10)
 *         .filter(list);
 *
 * }</pre>
 * <p>
 * The propertyNames can traverse the object graph (e.g. customer.name) by using
 * dot notation. If any point during the object graph traversal to get a
 * property value is null then null is returned.
 * </p>
 * <p>
 * <pre>{@code
 *
 * // examples of property names that
 * // ... will traverse the object graph
 * // ... where customer is a property of our bean
 *
 * customer.name
 * customer.shippingAddress.city
 *
 * }</pre>
 * <p>
 * <pre>{@code
 *
 * // get a list of entities (query execution statistics)
 *
 * List<Order> orders =
 *     DB.find(Order.class).findList();
 *
 * // Apply a filter...
 *
 * List<Order> filteredOrders =
 *     DB.filter(Order.class)
 *         .startsWith("customer.name", "Rob")
 *         .eq("customer.shippingAddress.city", "Auckland")
 *         .filter(orders);
 *
 * }</pre>
 *
 * @param <T> the entity bean type
 */
public interface Filter<T> extends QueryDsl<T, Filter<T>> {

  /**
   * Matcher to test if a bean will match. This holds a context internally, so that matching will happen efficient.
   *
   * @author Roland Praml, FOCONIS AG
   *
   * @param <T> the entity bean type.
   */
  interface Matcher<T> {
    boolean match(T bean);
  }

  /**
   * Specify a sortByClause.
   * <p>
   * The sort (if specified) will always execute first followed by the filter
   * expressions.
   * </p>
   * <p>
   * Refer to {@link DB#sort(List, String)} for more detail.
   * </p>
   */
  Filter<T> sort(String sortByClause);

  /**
   * Specify the first row to return.
   */
  Filter<T> firstRow(int firstRow);

  /**
   * Specify the maximum number of rows/elements to return.
   */
  Filter<T> maxRows(int maxRows);

  /**
   * Apply the filter to the list returning a new list of the matching elements
   * in the sorted order.
   * <p>
   * The sourceList will remain unmodified.
   * </p>
   *
   * @return Returns a new list with the sorting and filters applied.
   */
  List<T> filter(List<T> sourceList);

  /**
   * returns the firstRow setting.
   */
  int getFirstRow();

  /**
   * retrns the maxRow setting.
   */
  int getMaxRows();

  /**
   * returns the sortByClause.
   */
  String getSort();

  /**
   * Returns a matcher.
   */
  Matcher<T> matcher();

}
