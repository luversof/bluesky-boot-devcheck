package io.github.luversof.boot.devcheck.service;

import io.github.luversof.boot.devcheck.annotation.DevCheckUtil;

@DevCheckUtil
public final class SampleDevCheckUtil {

	private SampleDevCheckUtil() {
	}

	public static String echo(String value) {
		return value;
	}
}
