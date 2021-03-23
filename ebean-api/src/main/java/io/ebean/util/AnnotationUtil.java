package io.ebean.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Annotation utility methods to find annotations.
 */
public class AnnotationUtil {

  private static final Map<AnnotatedElement, AnnotationMeta> annotationCache = new ConcurrentHashMap<>();

  /**
   * Caches all annotations for an annotated element.
   */
  static class AnnotationMeta {

    final Map<Class<? extends Annotation>, Set<Annotation>> annotations = new HashMap<>();

    AnnotationMeta(AnnotatedElement elem) {
      Annotation[] anns = elem.getAnnotations();
      Set<Annotation> visited = new HashSet<>();

      if (elem instanceof Class) {
        Class<?> clazz = (Class<?>) elem;
        do {
          scanAnnotations(clazz.getAnnotations(), visited);
          clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);
      } else {
        scanAnnotations(anns, visited);
      }

      // seal the metadata
      for (Map.Entry<Class<? extends Annotation>, Set<Annotation>> entry : annotations.entrySet()) {
        entry.setValue(Collections.unmodifiableSet(entry.getValue()));
      }
    }

    <A extends Annotation> void scanAnnotations(A[] anns, Set<Annotation> visited) {
      for (Annotation ann : anns) {
        if (visited.add(ann) && notJavaLang(ann)) {
          Class<? extends Annotation> type = ann.annotationType();
          annotations.computeIfAbsent(type, k -> new LinkedHashSet<>()).add(ann);
          scanAnnotations(type.getAnnotations(), visited);
        }
      }
    }

    @SuppressWarnings("unchecked")
    <A extends Annotation> Set<A> findAnnotations(Class<A> annotationType) {
      return (Set<A>) annotations.getOrDefault(annotationType, Collections.emptySet());
    }

    @SuppressWarnings("unchecked")
    <A extends Annotation> A findAnnotation(Class<A> annotationType) {
      Set<Annotation> set = annotations.get(annotationType);
      return set == null ? null : (A) set.iterator().next();
    }

    Set<Annotation> finaAllMetaAnnotations(Set<Class<?>> annotationTypes) {
      return Collections.unmodifiableSet(
        annotationTypes.stream()
          .map(it -> annotations.getOrDefault(it, Collections.emptySet()))
          .flatMap(Collection::stream)
          .collect(Collectors.toSet()));
    }
  }

  /**
   * Determine if the supplied {@link Annotation} is defined in the core JDK {@code java.lang.annotation} package.
   */
  public static boolean notJavaLang(Annotation annotation) {
    return !annotation.annotationType().getName().startsWith("java.lang.annotation");
  }

  /**
   * Simple get on field or method with no meta-annotations or platform filtering.
   */
  public static <A extends Annotation> A get(AnnotatedElement element, Class<A> annotation) {
    return annotationCache.computeIfAbsent(element, AnnotationMeta::new).findAnnotation(annotation);
  }

  /**
   * Simple has with no meta-annotations or platform filtering.
   */
  public static <A extends Annotation> boolean has(AnnotatedElement element, Class<A> annotation) {
    return get(element, annotation) != null;
  }

  /**
   * On class get the annotation - includes inheritance.
   */
  public static <A extends Annotation> A typeGet(Class<?> clazz, Class<A> annotationType) {
    return annotationCache.computeIfAbsent(clazz, AnnotationMeta::new).findAnnotation(annotationType);
  }

  /**
   * On class get all the annotations - includes inheritance.
   */
  public static <A extends Annotation> Set<A> typeGetAll(Class<?> clazz, Class<A> annotationType) {
    return annotationCache.computeIfAbsent(clazz, AnnotationMeta::new).findAnnotations(annotationType);
  }

  /**
   * On class simple check for annotation - includes inheritance.
   */
  public static <A extends Annotation> boolean typeHas(Class<?> clazz, Class<A> annotation) {
    return typeGet(clazz, annotation) != null;
  }

  /**
   * Find all the annotations for the filter searching meta-annotations.
   */
  public static Set<Annotation> metaFindAllFor(AnnotatedElement element, Set<Class<?>> filter) {
    return annotationCache.computeIfAbsent(element, AnnotationMeta::new).finaAllMetaAnnotations(filter);
  }

}
