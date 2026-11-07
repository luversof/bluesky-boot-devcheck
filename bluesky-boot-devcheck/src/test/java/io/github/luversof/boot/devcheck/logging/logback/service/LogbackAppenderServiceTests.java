package io.github.luversof.boot.devcheck.logging.logback.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class LogbackAppenderServiceTests {

  @Test
  void keepsOnlyTheNewestLogsUpToCapacity() {
    var service = new LogbackAppenderService(3);
    IntStream.rangeClosed(1, 5).forEach(i -> service.addLog("log" + i));

    assertThat(service.getLogs()).containsExactly("log3", "log4", "log5");
  }

  @Test
  void concurrentWritersNeitherFailNorOverflow() throws Exception {
    var service = new LogbackAppenderService(100);
    var tasks = new ArrayList<Callable<Void>>();
    for (int thread = 0; thread < 8; thread++) {
      int t = thread;
      tasks.add(() -> {
        for (int i = 0; i < 5_000; i++) {
          service.addLog(t + "-" + i);
          service.getLogs();
        }
        return null;
      });
    }
    try (var executor = Executors.newFixedThreadPool(8)) {
      for (Future<Void> future : executor.invokeAll(tasks)) {
        // rethrows NoSuchElementException etc. from the writer
        future.get();
      }
    }

    assertThat(service.getLogs()).hasSize(100).doesNotContainNull();
  }

  @Test
  void returnedListIsASnapshot() {
    var service = new LogbackAppenderService(2);
    service.addLog("a");
    var snapshot = service.getLogs();
    service.addLog("b");

    assertThat(snapshot).containsExactly("a");
  }
}
