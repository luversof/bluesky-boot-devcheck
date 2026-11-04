package io.github.luversof.boot.devcheck.logging.logback.service;

import java.util.ArrayDeque;
import java.util.List;

/**
 * 최근 로그 몇 줄을 보관한다. 여러 스레드가 동시에 로그를 남기므로 넣기와 읽기를 한 잠금으로 묶는다.
 *
 * <p>예전에는 잠금 없이 "크기 확인 &rarr; 하나 빼기 &rarr; 넣기" 를 해서 동시에 들어온 로그가 조용히 빠지거나 {@code NoSuchElementException}
 * 이 났고, 쓰지 않는 로그 이벤트 객체까지 붙잡고 있었다.
 */
public class LogbackAppenderService {

	public static final int DEFAULT_CAPACITY = 500;

	private final int capacity;

	private final ArrayDeque<String> logs;

	public LogbackAppenderService() {
		this(DEFAULT_CAPACITY);
	}

	public LogbackAppenderService(int capacity) {
		this.capacity = capacity;
		this.logs = new ArrayDeque<>(capacity);
	}

	public synchronized void addLog(String logMessage) {
		if (logs.size() == capacity) {
			logs.removeFirst();
		}
		logs.addLast(logMessage);
	}

	/** 오래된 것부터 최근 것 순서의 사본. */
	public synchronized List<String> getLogs() {
		return List.copyOf(logs);
	}

}
