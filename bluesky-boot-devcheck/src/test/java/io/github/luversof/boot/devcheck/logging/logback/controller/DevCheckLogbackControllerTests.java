package io.github.luversof.boot.devcheck.logging.logback.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.devcheck.logging.logback.service.LogbackAppenderService;

class DevCheckLogbackControllerTests {

  @Test
  void keepsLineBreaksInsideOneLogAndDropsTheTrailingOne() {
    var service = new LogbackAppenderService();
    service.addLog("ERROR boom\n\tat a.B.c(B.java:1)\n\tat a.D.e(D.java:2)\r\n");

    assertThat(new DevCheckLogbackController(service).logView())
        .containsExactly("ERROR boom\n\tat a.B.c(B.java:1)\n\tat a.D.e(D.java:2)");
  }
}
