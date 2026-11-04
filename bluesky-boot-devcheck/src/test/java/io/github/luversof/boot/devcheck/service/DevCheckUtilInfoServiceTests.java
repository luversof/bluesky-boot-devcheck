package io.github.luversof.boot.devcheck.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.devcheck.DevCheckProperties;

class DevCheckUtilInfoServiceTests {

	@Test
	void unsetBasePackagesGiveAnEmptyList() {
		// base-packages 를 안 쓰면 예전에는 NPE 가 났다
		var service = new DevCheckUtilInfoService(new DevCheckProperties());

		assertThat(service.getDevCheckUtilInfo()).isEmpty();
	}

	@Test
	void findsAnnotatedUtilsOnceAndCachesThem() {
		var properties = new DevCheckProperties();
		properties.getBasePackages().add("io.github.luversof.boot.devcheck.service");
		var service = new DevCheckUtilInfoService(properties);

		var first = service.getDevCheckUtilInfo();
		assertThat(first).extracting("methodName").containsExactly("SampleDevCheckUtil");
		assertThat(first.get(0).methodInfoList()).extracting("method").containsExactly("echo");
		assertThat(service.getDevCheckUtilInfo()).isSameAs(first);
	}
}
