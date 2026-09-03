package io.github.luversof.boot.autoconfigure.devcheck;

import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_CORE_SERVLET_USER_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_SERVLET_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.devcheck.controller.DevCheckConfigurationMetadataController;
import io.github.luversof.boot.devcheck.controller.DevCheckCoreController;

class DevCheckCoreAutoServletConfigurationTests {
	
	private static final Logger log = LoggerFactory.getLogger(DevCheckCoreAutoServletConfigurationTests.class);
	
	private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
			.withPropertyValues(BASE_PROPERTY)
			.withConfiguration(AutoConfigurations.of(DEVCHECK_SERVLET_CONFIGURATION))
			.withUserConfiguration(DEVCHECK_CORE_SERVLET_USER_CONFIGURATION)
			;
	
	@Test
	void hasCoreDevCheckController() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(DevCheckCoreController.class);
		});
	}
	
	@Test
	void hasConfigurationMetadataController() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(DevCheckConfigurationMetadataController.class);
			var metadata = context.getBean(DevCheckConfigurationMetadataController.class).configurationMetadata();
			assertThat(metadata).containsKeys("sources", "documents");
			assertThat((java.util.List<?>) metadata.get("documents")).isNotEmpty();
			log.debug("configuration metadata documents : {}", ((java.util.List<?>) metadata.get("sources")).size());
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	void applicationPropertiesReturnsConfiguredValuesForRequestedPrefix() {
		this.contextRunner
				.withPropertyValues("bluesky-boot.core.module-name-set=a,b", "bluesky-boot.core.some-password=verysecret",
						"bluesky-boot.core.raw-source=resolved-value",
						"bluesky-boot.core.placeholder-sample=${bluesky-boot.core.raw-source}")
				.run(context -> {
					var controller = context.getBean(DevCheckConfigurationMetadataController.class);

					var body = controller.applicationProperties(java.util.List.of("bluesky-boot.core"));
					var properties = (java.util.List<java.util.Map<String, Object>>) body.get("properties");
					var byName = new java.util.HashMap<String, Object>();
					properties.forEach(entry -> byName.put((String) entry.get("name"), entry.get("value")));

					assertThat(byName).containsEntry("bluesky-boot.core.module-name-set", "a,b");

					// the file value is reported as written; the resolved one comes separately
					var placeholder = properties.stream()
							.filter(entry -> "bluesky-boot.core.placeholder-sample".equals(entry.get("name")))
							.findFirst()
							.orElseThrow();
					assertThat(placeholder.get("value")).isEqualTo("${bluesky-boot.core.raw-source}");
					assertThat(placeholder.get("resolvedValue")).isEqualTo("resolved-value");
					assertThat(byName).containsEntry("bluesky-boot.core.some-password", "****");
					assertThat(byName.keySet()).allMatch(name -> name.startsWith("bluesky-boot.core"));

					// no prefix requested -> nothing, so the whole environment is never dumped
					var empty = (java.util.List<?>) controller.applicationProperties(null).get("properties");
					assertThat(empty).isEmpty();
				});
	}

	@Test
	void configurationMetadataIsMappedUnderCheckPrefix() {
		this.contextRunner.run(context -> {
			var patterns = context.getBean(RequestMappingHandlerMapping.class).getHandlerMethods().keySet().stream()
					.map(Object::toString).toList();
			assertThat(patterns).anyMatch(p -> p.contains("/_check/devcheck/metadata/configurationMetadata"));
		});
	}

	@Test
	void handlerMethods() {
		this.contextRunner.run(context -> {
			var handlerMethods = context.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
			log.debug("handlerMethods : {}", handlerMethods);
		});
	}
}
