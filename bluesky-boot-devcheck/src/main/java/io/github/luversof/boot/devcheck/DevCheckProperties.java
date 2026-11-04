package io.github.luversof.boot.devcheck;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bluesky-boot.dev-check")
public class DevCheckProperties {

	/**
	 * dev-check 화면과 API 를 켤지. 설정 값과 로그를 보여 주므로 명시했을 때만 켠다.<br>
	 * Whether to enable the dev-check pages and APIs. They expose configuration values and logs, so they are off unless set.
	 */
	private boolean enabled;

	/**
	 * 검색할 package 를 지정합니다.<br>static util class 검색 시 사용합니다.<br>여러 개를 지정할 수 있습니다.<br>
	 * Specifies the package to search for.<br>Used when searching for static util class.<br>You can specify multiple.<br>
	 */
	private List<String> basePackages = new ArrayList<>();

	/** dev-check 화면과 API 의 경로 앞부분. */
	private String[] pathPrefixes = { "/_check" };

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<String> getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(List<String> basePackages) {
		this.basePackages = basePackages;
	}

	public String[] getPathPrefixes() {
		return pathPrefixes;
	}

	public void setPathPrefixes(String[] pathPrefixes) {
		this.pathPrefixes = pathPrefixes;
	}

}
