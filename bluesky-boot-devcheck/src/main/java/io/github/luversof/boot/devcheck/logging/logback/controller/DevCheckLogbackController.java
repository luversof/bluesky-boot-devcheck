package io.github.luversof.boot.devcheck.logging.logback.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppenderService;

@DevCheckController
@RequestMapping(value = "/devcheck/logging/logback", produces = MediaType.APPLICATION_JSON_VALUE)
public class DevCheckLogbackController {

  private final LogbackAppenderService logbackAppenderService;

  public DevCheckLogbackController(LogbackAppenderService logbackAppenderService) {
    this.logbackAppenderService = logbackAppenderService;
  }

  @DevCheckDescription("최근 로그 500줄 조회")
  @GetMapping("/logView")
  public List<String> logView() {
    // 끝 줄바꿈만 뗀다 - 안쪽 줄바꿈까지 지우면 스택 트레이스 프레임이 한 줄로 붙는다
    return logbackAppenderService.getLogs().stream().map(String::stripTrailing).toList();
  }
}
