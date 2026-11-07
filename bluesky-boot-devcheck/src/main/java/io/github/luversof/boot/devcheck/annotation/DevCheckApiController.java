package io.github.luversof.boot.devcheck.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DevCheck Api를 제공하기 위한 annotation
 *
 * <p>내부 사용 목적
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public @interface DevCheckApiController {

  @AliasFor(annotation = RestController.class)
  String value() default "";
}
