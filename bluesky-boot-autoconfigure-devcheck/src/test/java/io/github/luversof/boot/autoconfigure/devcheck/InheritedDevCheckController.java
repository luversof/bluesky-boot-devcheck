package io.github.luversof.boot.autoconfigure.devcheck;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@DevCheckController
public @interface InheritedDevCheckController {}
