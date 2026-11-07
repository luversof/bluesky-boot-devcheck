package io.github.luversof.boot.devcheck.logging.logback.service;

import java.nio.charset.StandardCharsets;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.AppenderAttachable;

/** 로그를 {@link LogbackAppenderService} 에 넣는 appender. */
public class LogbackAppender<E> extends UnsynchronizedAppenderBase<E> {

	private final LogbackAppenderService logbackAppenderService;

	private Encoder<E> encoder;

	private AppenderAttachable<E> attachedTo;

	public LogbackAppender(LogbackAppenderService logbackAppenderService) {
		this.logbackAppenderService = logbackAppenderService;
	}

	public void setEncoder(Encoder<E> encoder) {
		this.encoder = encoder;
	}

	@Override
	protected void append(E eventObject) {
		if (!isStarted()) {
			return;
		}
		logbackAppenderService.addLog(format(eventObject));
	}

	private String format(E eventObject) {
		// layout 이 있으면 문자열을 그대로 받는다 - 바이트로 바꿨다 되돌리면 문자셋이 어긋날 수 있다
		if (encoder instanceof LayoutWrappingEncoder<E> layoutEncoder && layoutEncoder.getLayout() != null) {
			return layoutEncoder.getLayout().doLayout(eventObject);
		}
		return new String(encoder.encode(eventObject), StandardCharsets.UTF_8);
	}

	/** 로거에 붙이고 시작한다. */
	public void attachTo(AppenderAttachable<E> logger) {
		start();
		logger.addAppender(this);
		this.attachedTo = logger;
	}

	/** 붙인 로거에서 떼고 멈춘다. 컨텍스트가 닫힐 때 불린다 - 안 떼면 컨텍스트를 다시 띄울 때마다 appender 가 쌓인다. */
	public void detach() {
		if (attachedTo != null) {
			attachedTo.detachAppender(this);
			attachedTo = null;
		}
		stop();
	}

}
