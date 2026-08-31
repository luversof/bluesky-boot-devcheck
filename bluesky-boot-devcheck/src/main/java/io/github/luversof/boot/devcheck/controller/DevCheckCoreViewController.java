package io.github.luversof.boot.devcheck.controller;

import java.time.Duration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckViewController;

@DevCheckViewController
public class DevCheckCoreViewController {
	
	@GetMapping("/index")
	public ResponseEntity<Resource> index() {
		return forwardToStaticResource("static/_check/devCheckInfo.html");
	}

	@GetMapping("/util")
	public ResponseEntity<Resource> util() {
		return forwardToStaticResource("static/_check/devCheckUtilInfo.html");
	}

	@GetMapping("/jsonView")
	public ResponseEntity<Resource> jsonView() {
		return forwardToStaticResource("static/_check/devCheckJsonView.html");
	}

	private ResponseEntity<Resource> forwardToStaticResource(String resourcePath) {
		ClassPathResource resource = new ClassPathResource(resourcePath);
		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).cacheControl(CacheControl.maxAge(Duration.ofDays(365))).body(resource);
	}
}