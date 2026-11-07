package io.github.luversof.boot.devcheck.controller;

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

  @GetMapping("/propertiesView")
  public ResponseEntity<Resource> propertiesView() {
    return forwardToStaticResource("static/_check/devCheckPropertiesView.html");
  }

  private ResponseEntity<Resource> forwardToStaticResource(String resourcePath) {
    ClassPathResource resource = new ClassPathResource(resourcePath);
    if (!resource.exists()) {
      return ResponseEntity.notFound().build();
    }
    // 라이브러리를 올리면 화면도 바로 바뀌어야 한다 - 예전 365일 캐시는 옛 화면을 붙잡았다
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_HTML)
        .cacheControl(CacheControl.noCache())
        .body(resource);
  }
}
