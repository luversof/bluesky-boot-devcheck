package io.github.luversof.boot.devcheck.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;

/**
 * Serves the Spring configuration metadata found on the classpath.
 *
 * <p>The metadata is generated at build time by the configuration processor and
 * describes every supported property (name, type, default value, description).
 * The properties viewer uses it for key completion and validation.
 *
 * <p>Documents are returned verbatim - this module has no JSON parser on its
 * compile classpath, so merging is left to the caller.
 *
 * @author bluesky
 */
@DevCheckController
@RequestMapping(value = "/devcheck/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
public class DevCheckConfigurationMetadataController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DevCheckConfigurationMetadataController.class);

  private static final String[] METADATA_LOCATIONS = {
    "classpath*:META-INF/spring-configuration-metadata.json",
    "classpath*:META-INF/additional-spring-configuration-metadata.json"
  };

  /** Property names that look like a secret are reported masked. */
  private static final String[] SECRET_HINTS = {
    "password", "secret", "token", "credential", "private-key", "privatekey", "passphrase"
  };

  private final ConfigurableEnvironment environment;

  public DevCheckConfigurationMetadataController(ConfigurableEnvironment environment) {
    this.environment = environment;
  }

  @DevCheckDescription("spring-configuration-metadata 조회 (properties viewer용)")
  @GetMapping("/configurationMetadata")
  public Map<String, Object> configurationMetadata() {
    var sources = new ArrayList<String>();
    var documents = new ArrayList<String>();

    var resolver = new PathMatchingResourcePatternResolver();
    for (String location : METADATA_LOCATIONS) {
      Resource[] resources;
      try {
        resources = resolver.getResources(location);
      } catch (IOException e) {
        LOGGER.debug("configuration metadata not readable : {}", location, e);
        continue;
      }
      for (Resource resource : resources) {
        try {
          documents.add(resource.getContentAsString(StandardCharsets.UTF_8));
          sources.add(resource.getDescription());
        } catch (IOException e) {
          LOGGER.debug("failed to read configuration metadata : {}", resource, e);
        }
      }
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("sources", sources);
    result.put("documents", documents);
    return result;
  }

  /**
   * Properties actually configured for this application, limited to the requested
   * prefixes so the response stays about the framework instead of the whole
   * environment. Values are the resolved ones and the source is the winning
   * property source.
   *
   * @param prefix property name prefixes to include; nothing is returned without one
   */
  @DevCheckDescription("현재 애플리케이션에 설정된 properties 조회 (prefix 지정 필요)")
  @GetMapping("/applicationProperties")
  public Map<String, Object> applicationProperties(
      @RequestParam(name = "prefix", required = false) List<String> prefix) {
    var properties = new ArrayList<Map<String, Object>>();
    var seen = new LinkedHashSet<String>();

    if (prefix != null && !prefix.isEmpty()) {
      for (PropertySource<?> propertySource : environment.getPropertySources()) {
        if (!(propertySource instanceof EnumerablePropertySource<?> enumerable)) {
          continue;
        }
        String[] names;
      try {
        names = enumerable.getPropertyNames();
      } catch (RuntimeException e) {
        LOGGER.debug("property source not enumerable : {}", propertySource.getName(), e);
        continue;
      }
      for (String name : names) {
          if (!matchesPrefix(name, prefix) || !seen.add(name)) {
            continue;
          }
          var secret = isSecret(name);

          Object raw;
          try {
            raw = enumerable.getProperty(name);
          } catch (RuntimeException e) {
            LOGGER.debug("property not readable : {}", name, e);
            continue;
          }

          // resolving can fail on an unresolvable placeholder - that must not fail the call
          String resolved;
          try {
            resolved = environment.getProperty(name);
          } catch (RuntimeException e) {
            LOGGER.debug("property not resolvable : {}", name, e);
            resolved = null;
          }

          var entry = new LinkedHashMap<String, Object>();
          entry.put("name", name);
          // as written in the file: placeholders and expressions are left untouched
          entry.put("value", secret ? "****" : (raw == null ? null : String.valueOf(raw)));
          // what the application ends up using once placeholders are resolved
          entry.put("resolvedValue", secret ? "****" : resolved);
          entry.put("source", propertySource.getName());
          entry.put("kind", sourceKind(propertySource.getName()));
          properties.add(entry);
        }
      }
    }

    var result = new LinkedHashMap<String, Object>();
    result.put("properties", properties);
    return result;
  }

  private boolean matchesPrefix(String name, List<String> prefixes) {
    for (String candidate : prefixes) {
      if (name.equals(candidate) || name.startsWith(candidate + ".")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Where a value was written by hand: "config" for application.properties / yaml
   * (including profile specific files), "system" for JVM and OS supplied values.
   */
  private String sourceKind(String sourceName) {
    if (sourceName == null) {
      return "other";
    }
    if (sourceName.startsWith("Config resource") || sourceName.contains("applicationConfig")) {
      return "config";
    }
    if (sourceName.startsWith("system") || sourceName.contains("systemEnvironment")
        || sourceName.contains("systemProperties") || sourceName.contains("commandLineArgs")) {
      return "system";
    }
    return "other";
  }

  private boolean isSecret(String name) {
    var lower = name.toLowerCase();
    for (String hint : SECRET_HINTS) {
      if (lower.contains(hint)) {
        return true;
      }
    }
    return false;
  }
}
