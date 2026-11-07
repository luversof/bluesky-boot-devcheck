package io.github.luversof.boot.autoconfigure.devcheck;

import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_SERVLET_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.github.luversof.boot.autoconfigure.devcheck.logging.logback.LogbackAutoConfiguration;
import io.github.luversof.boot.autoconfigure.devcheck.servlet.DevCheckMvcAutoConfiguration;
import io.github.luversof.boot.devcheck.DevCheckProperties;
import io.github.luversof.boot.devcheck.controller.DevCheckCoreController;
import io.github.luversof.boot.devcheck.logging.logback.controller.DevCheckLogbackController;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppenderService;

class DevCheckEnabledTests {

  private static final String APPENDER_NAME = "blueskyBootLogbackAppender";

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(DEVCHECK_SERVLET_CONFIGURATION))
          .withConfiguration(
              AutoConfigurations.of(
                  DevCheckAutoConfiguration.class,
                  DevCheckMvcAutoConfiguration.class,
                  LogbackAutoConfiguration.class));

  @Test
  void offUnlessEnabled() {
    this.contextRunner.run(context -> {
      assertThat(context).hasNotFailed();
      assertThat(context).doesNotHaveBean(DevCheckProperties.class);
      assertThat(context).doesNotHaveBean(DevCheckCoreController.class);
      assertThat(context).doesNotHaveBean(DevCheckLogbackController.class);
      assertThat(rootLogger().getAppender(APPENDER_NAME)).isNull();
    });
  }

  @Test
  void appDevCheckControllerIsRegisteredOnlyWhenEnabled() {
    // 앱이 만든 @DevCheckController 는 자동 설정 밖이라, 꺼진 상태에서도 접두어 없이 열리면 안 된다
    this.contextRunner
        .withUserConfiguration(TestDevCheckController.class)
        .run(context -> assertThat(context).doesNotHaveBean(TestDevCheckController.class));
    this.contextRunner
        .withUserConfiguration(TestDevCheckController.class)
        .withPropertyValues("bluesky-boot.dev-check.enabled=true")
        .run(context -> assertThat(context).hasSingleBean(TestDevCheckController.class));
  }

  @Test
  void enabledRegistersControllersAndCapturesLogsUntilClosed() {
    this.contextRunner
        .withPropertyValues("bluesky-boot.dev-check.enabled=true")
        .run(context -> {
          assertThat(context).hasSingleBean(DevCheckCoreController.class);
          assertThat(context).hasSingleBean(DevCheckLogbackController.class);
          assertThat(rootLogger().getAppender(APPENDER_NAME)).isNotNull();

          LoggerFactory.getLogger(DevCheckEnabledTests.class).warn("dev-check-capture-marker");
          assertThat(context.getBean(LogbackAppenderService.class).getLogs())
              .anyMatch(log -> log.contains("dev-check-capture-marker"));
        });
    // 컨텍스트를 닫으면 떼어 내야 한다 - 안 그러면 테스트·재기동마다 쌓인다
    assertThat(rootLogger().getAppender(APPENDER_NAME)).isNull();
  }

  @Test
  void renamedBasePackagesBinds() {
    this.contextRunner
        .withPropertyValues(
            "bluesky-boot.dev-check.enabled=true", "bluesky-boot.dev-check.base-packages=a.b,c.d")
        .run(context ->
            assertThat(context.getBean(DevCheckProperties.class).getBasePackages())
                .containsExactly("a.b", "c.d"));
  }

  @Test
  void repeatedContextsDoNotStackAppenders() {
    var counts = new ArrayList<Integer>();
    for (int i = 0; i < 3; i++) {
      this.contextRunner
          .withPropertyValues("bluesky-boot.dev-check.enabled=true")
          .run(context -> counts.add(countAppenders()));
    }
    assertThat(counts).isEqualTo(List.of(1, 1, 1));
  }

  private static Logger rootLogger() {
    return ((LoggerContext) LoggerFactory.getILoggerFactory())
        .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
  }

  private static int countAppenders() {
    var count = 0;
    for (var it = rootLogger().iteratorForAppenders(); it.hasNext(); ) {
      if (APPENDER_NAME.equals(it.next().getName())) {
        count++;
      }
    }
    return count;
  }
}
