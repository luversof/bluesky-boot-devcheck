package io.github.luversof.boot.devcheck.controller;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;

@DevCheckController
@RequestMapping(value = "/devcheck/core", produces = MediaType.APPLICATION_JSON_VALUE)
public class DevCheckCoreController {
	
	private final ApplicationContext applicationContext;

	public DevCheckCoreController(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@DevCheckDescription("spring activeProfiles")
	@GetMapping("/activeProfiles")
	public String[] activeProfiles() {
		return applicationContext.getEnvironment().getActiveProfiles();
	}

	@DevCheckDescription("locale")
	@GetMapping("/locale")
	public Locale locale() {
		return LocaleContextHolder.getLocale();
	}

	@DevCheckDescription("LocaleDateTime.now()")
	@GetMapping("/localDateTimeNow")
	public LocalDateTime localeDateTimeNow() {
		return LocalDateTime.now();
	}

	@DevCheckDescription("ZonedDateTime.now()")
	@GetMapping("/zonedDateTimeNow")
	public ZonedDateTime zonedDateTimeNow() {
		return ZonedDateTime.now();
	}

}