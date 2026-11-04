package io.github.luversof.boot.devcheck.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DevCheckCoreViewControllerTests {

	private final DevCheckCoreViewController controller = new DevCheckCoreViewController();

	@Test
	void pagesAreRevalidatedOnEveryLoad() {
		// 예전에는 365일 캐시라 라이브러리를 올려도 옛 화면이 남았다
		for (var entity : List.of(controller.index(), controller.util(), controller.jsonView(), controller.propertiesView())) {
			assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(entity.getHeaders().getCacheControl()).isEqualTo("no-cache");
		}
	}
}
