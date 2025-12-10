package io.github.luversof.boot.devcheck;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bluesky-boot.dev-check")
public class DevCheckProperties {

	private boolean enabled;

	/**
	 * 검색할 package 를 지정합니다.<br>static util class 검색 시 사용합니다.<br>여러 개를 지정할 수 있습니다.<br>
	 * Specifies the package to search for.<br>Used when searching for static util class.<br>You can specify multiple.<br>
	 */
	private List<String> basePackageList;
	
	private String[] pathPrefixes;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<String> getBasePackageList() {
		return basePackageList;
	}

	public void setBasePackageList(List<String> basePackageList) {
		this.basePackageList = basePackageList;
	}

	public String[] getPathPrefixes() {
		return pathPrefixes;
	}

	public void setPathPrefixes(String[] pathPrefixes) {
		this.pathPrefixes = pathPrefixes;
	}
	
}
