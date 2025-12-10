package io.github.luversof.boot.devcheck.logging.logback.service;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

public class LogbackAppender<E> extends UnsynchronizedAppenderBase<E> {

	private LogbackAppenderService<E> blueskyLogbackAppenderService;

	public LogbackAppender(LogbackAppenderService<E> blueskyLogbackAppenderService) {
		this.blueskyLogbackAppenderService = blueskyLogbackAppenderService;
	}

	private Encoder<E> encoder;
	
	public void setEncoder(Encoder<E> encoder) {
		this.encoder = encoder;
	}

	@Override
	protected void append(E eventObject) {
		if (!isStarted()) {
			return;
		}
		blueskyLogbackAppenderService.addLog(eventObject, new String(encoder.encode(eventObject)));
	}

}
