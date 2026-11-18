package io.github.luversof.boot.autoconfigure.devcheck;

import java.util.HashSet;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;

import io.github.luversof.boot.autoconfigure.devcheck.servlet.DevCheckMvcAutoConfiguration;

public final class AutoConfigurationTestInfo {

  private AutoConfigurationTestInfo() {}

  public static final String[] BASE_PROPERTY = {
    "net-profile=localdev", "bluesky-boot.dev-check.enabled=true"
  };

  public static final Class<?>[] DEVCHECK_SERVLET_CONFIGURATION = {
    WebMvcAutoConfiguration.class,
    DispatcherServletAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class,
    PropertyPlaceholderAutoConfiguration.class
  };

  public static final Class<?>[] DEVCHECK_CORE_USER_CONFIGURATION = {
    DevCheckAutoConfiguration.class
  };

  public static final Class<?>[] DEVCHECK_CORE_SERVLET_USER_CONFIGURATION =
      addClassAll(DEVCHECK_CORE_USER_CONFIGURATION, DevCheckMvcAutoConfiguration.class);

  public static Class<?>[] addClassAll(Class<?>[] target, Class<?>... classes) {
    var set = new HashSet<>();

    for (var clazz : target) {
      set.add(clazz);
    }

    for (var clazz : classes) {
      set.add(clazz);
    }
    return set.toArray(new Class<?>[set.size()]);
  }
}
