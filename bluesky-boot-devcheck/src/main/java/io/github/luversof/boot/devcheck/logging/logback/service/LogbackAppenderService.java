package io.github.luversof.boot.devcheck.logging.logback.service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogbackAppenderService<E> {

	private static final int QUEUE_SIZE = 500;

	private Queue<LogObject<E>> logQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
	
	public Queue<LogObject<E>> getLogQueue() {
		return logQueue;
	}

	public void addLog(E eventObject, String logMessage) {
		if (logQueue.size() >= QUEUE_SIZE) {
			logQueue.remove();
		}
		logQueue.offer(new LogObject<>(eventObject, logMessage));
	}

	public static class LogObject<E> {
		
		E eventObject;
		String logMessage;
		
		public LogObject(E eventObject, String logMessage) {
			super();
			this.eventObject = eventObject;
			this.logMessage = logMessage;
		}

		public E getEventObject() {
			return eventObject;
		}

		public void setEventObject(E eventObject) {
			this.eventObject = eventObject;
		}

		public String getLogMessage() {
			return logMessage;
		}

		public void setLogMessage(String logMessage) {
			this.logMessage = logMessage;
		}
		
	}

}
