package io.github.luversof.boot.devcheck.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DevCheckDescription {

  /**
   * A brief description to be displayed on the '/_check' or '/_check/util' page
   *
   * @return descriptive text
   */
  String value() default "";

  /**
   * @return Whether description is exposed
   */
  boolean displayable() default true;
}
