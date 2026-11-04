package io.github.luversof.boot.autoconfigure.devcheck.logging.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import io.github.luversof.boot.devcheck.logging.logback.controller.DevCheckLogbackController;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppender;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppenderService;

@AutoConfiguration("blueskyBootDevCheckLoggingLogbackAutoConfiguration")
@ConditionalOnClass(LoggerContext.class)
@ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true")
public class LogbackAutoConfiguration {

	@Bean
	LogbackAppenderService blueskyBootLogbackAppenderService() {
		return new LogbackAppenderService();
	}

	/** 루트 로거에 붙인다. 컨텍스트가 닫히면 떼어 낸다. */
	@Bean(destroyMethod = "detach")
	LogbackAppender<ILoggingEvent> blueskyBootLogbackAppender(LogbackAppenderService logbackAppenderService) {
		var logbackAppender = new LogbackAppender<ILoggingEvent>(logbackAppenderService);
		logbackAppender.setName("blueskyBootLogbackAppender");
		// slf4j 가 logback 이 아닌 구현에 붙어 있으면 붙이지 않는다
		if (!(LoggerFactory.getILoggerFactory() instanceof LoggerContext loggerContext)) {
			return logbackAppender;
		}
		var rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		logbackAppender.setContext(loggerContext);
		// 파일 로그와 같은 모양으로 보이게 FILE appender 의 encoder 를 빌린다(FileAppender·RollingFileAppender 모두)
		if (rootLogger.getAppender("FILE") instanceof OutputStreamAppender<ILoggingEvent> fileAppender
				&& fileAppender.getEncoder() != null) {
			logbackAppender.setEncoder(fileAppender.getEncoder());
		} else {
			var encoder = new PatternLayoutEncoder();
			encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
			encoder.setContext(loggerContext);
			encoder.start();
			logbackAppender.setEncoder(encoder);
		}
		logbackAppender.attachTo(rootLogger);
		return logbackAppender;
	}

	@Bean
	DevCheckLogbackController blueskyBootDevCheckLogbackController(LogbackAppenderService logbackAppenderService) {
		return new DevCheckLogbackController(logbackAppenderService);
	}

}
