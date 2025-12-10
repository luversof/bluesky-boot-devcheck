package io.github.luversof.boot.devcheck.util;

import java.util.ArrayList;
import java.util.List;

public enum DevCheckUtilTestsSource2 {

	CASE_A("/_check", new ArrayList<>(List.of("/test2/_check/test", "/_check/test", "/test/_check/test")), "/_check/test")
	;
	
	private String pathPrefix;
	
	private List<String> urlList;
	
	private String expectedUrlListFirst;

	private DevCheckUtilTestsSource2(String pathPrefix, List<String> urlList, String expectedUrlListFirst) {
		this.pathPrefix = pathPrefix;
		this.urlList = urlList;
		this.expectedUrlListFirst = expectedUrlListFirst;
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public String getExpectedUrlListFirst() {
		return expectedUrlListFirst;
	}
	
}
