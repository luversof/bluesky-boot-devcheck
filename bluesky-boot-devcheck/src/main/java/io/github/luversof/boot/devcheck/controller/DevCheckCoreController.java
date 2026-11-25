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

  @DevCheckDescription("활성 프로필 조회")
  @GetMapping("/activeProfiles")
  public String[] activeProfiles() {
    return applicationContext.getEnvironment().getActiveProfiles();
  }

  @DevCheckDescription("현재 locale 조회")
  @GetMapping("/locale")
  public Locale locale() {
    return LocaleContextHolder.getLocale();
  }

  @DevCheckDescription("현재 LocalDateTime 조회")
  @GetMapping("/localDateTimeNow")
  public LocalDateTime localeDateTimeNow() {
    return LocalDateTime.now();
  }

  @DevCheckDescription("현재 ZonedDateTime 조회")
  @GetMapping("/zonedDateTimeNow")
  public ZonedDateTime zonedDateTimeNow() {
    return ZonedDateTime.now();
  }
}
