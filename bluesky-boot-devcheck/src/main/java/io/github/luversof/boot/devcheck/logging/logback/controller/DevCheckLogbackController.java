package io.github.luversof.boot.devcheck.logging.logback.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppenderService;

@DevCheckController
@RequestMapping(value = "/blueskyBoot/devcheck/logging/logback", produces = MediaType.APPLICATION_JSON_VALUE)
public class DevCheckLogbackController {
	
	private final LogbackAppenderService<ILoggingEvent> playncLogbackAppenderService;

	public DevCheckLogbackController(LogbackAppenderService<ILoggingEvent> playncLogbackAppenderService) {
		super();
		this.playncLogbackAppenderService = playncLogbackAppenderService;
	}

	@DevCheckDescription("Check last 500 line log")
	@GetMapping("/logView")
	public List<String> logView() {
		return playncLogbackAppenderService.getLogQueue().stream().map(queue -> queue.getLogMessage().replaceAll(CoreConstants.LINE_SEPARATOR, "").replace("\t", "")).toList();
	}
}
