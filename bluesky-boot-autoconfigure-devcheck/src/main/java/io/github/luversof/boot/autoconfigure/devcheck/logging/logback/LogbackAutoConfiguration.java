package io.github.luversof.boot.autoconfigure.devcheck.logging.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import io.github.luversof.boot.devcheck.logging.logback.controller.DevCheckLogbackController;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppender;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppenderService;

@Configuration(value = "blueskyBootDevCheckLoggingLogbackAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass(Appender.class)
@ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogbackAutoConfiguration {

	@Bean
	LogbackAppenderService<ILoggingEvent> blueskyBootLogbackAppenderService() {
		var logbackAppenderService = new LogbackAppenderService<ILoggingEvent>();
		addLogbackAppender(logbackAppenderService);
		return logbackAppenderService;
	}

	private void addLogbackAppender(LogbackAppenderService<ILoggingEvent> logbackAppenderService) {
		var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		var appender = (RollingFileAppender<ILoggingEvent>) loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("FILE");
		var logbackAppender = new LogbackAppender<>(logbackAppenderService);
		logbackAppender.setContext(loggerContext);
		logbackAppender.setName("blueskyBootLogbackAppender");

		if (appender == null) {
			var encoder = new PatternLayoutEncoder();
			encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
			encoder.setContext(loggerContext);
			encoder.start();
			logbackAppender.setEncoder(encoder);
		} else {
			logbackAppender.setEncoder(appender.getEncoder());
		}
		

		logbackAppender.start();
		loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logbackAppender);
	}

	@Bean
	DevCheckLogbackController blueskyBootDevCheckLogbackController(LogbackAppenderService<ILoggingEvent> logbackAppenderService) {
		return new DevCheckLogbackController(logbackAppenderService);
	}
}
