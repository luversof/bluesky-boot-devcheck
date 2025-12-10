package io.github.luversof.boot.devcheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DevCheckUtilTests {
	
	@ParameterizedTest
	@EnumSource(DevCheckUtilTestsSource1.class)
	void getCurrentPathPrefix(DevCheckUtilTestsSource1 source) {
		String[] pathPrefixes =  source.getPathPrefixes();
		String currentRequestURL = source.getCurrentRequestURL();
		
		var expectedPathPrefix = DevCheckUtil.getCurrentPathPrefix(pathPrefixes, currentRequestURL);
		assertThat(expectedPathPrefix).isEqualTo(source.getExpectedPathPrefix());
	}
	
	@ParameterizedTest
	@EnumSource(DevCheckUtilTestsSource2.class)
	void sortUrlList(DevCheckUtilTestsSource2 source) {
		DevCheckUtil.sortUrlList(source.getUrlList(), source.getPathPrefix());
		
		assertThat(source.getUrlList().get(0)).isEqualTo(source.getExpectedUrlListFirst());
	}
}
