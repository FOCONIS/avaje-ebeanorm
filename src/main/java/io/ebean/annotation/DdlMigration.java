package io.ebean.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.ebean.Platform;



/**
 * Annotation to specify details for DDL-migration generation. (e.g. defaults/renames/...)
 * This annotation is <b>EXPERMIENTAL</b> and may change.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(DdlMigration.List.class)
public @interface DdlMigration {

  /**
   * SQLs that will be executed before the command.
   * 
   * Note: If you do not specify an SQL here, and this will alter the table, 
   * ebean will autogenerate a statement from default value like this:
   * <pre>
   * UPDATE table SET column = 'default' WHERE column IS NULL
   * </pre>
   */
  String[] preDdl() default {"${SET_DEFAULT}"};
  
  /**
   * SQLs that will be executed after the command
   */
  String[] postDdl() default {};

  
  /**
   * The defaultValue for new non-null columns.
   */
  String defaultValue() default "__UNSET__";
  
  /**
   * Specify the DDL version here (this is mainly for documentation)
   */
  String since() default "";
  
  /**
   * Specify for which platforms this DdlMigration takes place.
   */
  Platform[] platforms() default {};
  
  /**
   * Defines several {@link DdlMigration} annotations on the same element.
   */
  @Target(ElementType.FIELD)
  @Retention(RUNTIME)
  @Documented
  @interface List {

    DdlMigration[] value();
  }

}