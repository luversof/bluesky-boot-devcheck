package io.github.luversof.boot.devcheck.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * DevCheck ViewPage를 제공하기 위한 annotation
 *
 * <p>내부 사용 목적
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Controller
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public @interface DevCheckViewController {

  @AliasFor(annotation = Controller.class)
  String value() default "";
}
