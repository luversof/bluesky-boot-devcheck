package io.github.luversof.boot.autoconfigure.devcheck;

import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_CORE_SERVLET_USER_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_SERVLET_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.service.servlet.DevCheckInfoMvcService;
import jakarta.servlet.http.HttpServletRequest;

class DevCheckInfoMvcServiceTests {

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withPropertyValues(BASE_PROPERTY)
          .withConfiguration(AutoConfigurations.of(DEVCHECK_SERVLET_CONFIGURATION))
          .withUserConfiguration(DEVCHECK_CORE_SERVLET_USER_CONFIGURATION);

  @AfterEach
  void clearRequest() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void listsControllersEvenWhenTwoHandlerMappingsShareThem() {
    // 같은 컨트롤러를 보는 handler mapping 이 둘이면 예전에는 toMap 중복 키로 IllegalStateException
    this.contextRunner
        .withUserConfiguration(SecondHandlerMappingConfiguration.class)
        .run(context -> {
          assertThat(context.getBeansOfType(RequestMappingHandlerMapping.class))
              .hasSizeGreaterThan(1);
          RequestContextHolder.setRequestAttributes(
              new ServletRequestAttributes(new MockHttpServletRequest("GET", "/_check/index")));

          var service = context.getBean(DevCheckInfoMvcService.class);
          var list = service.getDevCheckInfoList();

          assertThat(list).isNotEmpty();
          assertThat(service.getDevCheckInfoList()).isSameAs(list);
        });
  }

  @Test
  void listsControllersWithAntPathMatcher() {
    this.contextRunner
        .withUserConfiguration(AntPathMatcherConfiguration.class)
        .run(context -> {
          // ant 방식이면 PathPattern 조건이 없다 - 예전에는 이 때문에 목록이 비었다
          assertThat(
                  context.getBean(RequestMappingHandlerMapping.class).getHandlerMethods().keySet())
              .isNotEmpty()
              .allMatch(info -> info.getPathPatternsCondition() == null);
          RequestContextHolder.setRequestAttributes(
              new ServletRequestAttributes(new MockHttpServletRequest("GET", "/_check/index")));

          assertThat(context.getBean(DevCheckInfoMvcService.class).getDevCheckInfoList())
              .isNotEmpty();
        });
  }

  @Test
  void urlsOfTheCurrentPrefixComeFirstUnderAContextPath() {
    this.contextRunner
        .withPropertyValues("bluesky-boot.dev-check.path-prefixes=/_check,/_check2")
        .run(context -> {
          // 요청 URI 에는 컨텍스트 경로가 붙고, /_check 는 /_check2 의 앞부분이기도 하다
          var request = new MockHttpServletRequest("GET", "/app/_check2/api/devCheckInfoList");
          request.setContextPath("/app");
          RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

          var list = context.getBean(DevCheckInfoMvcService.class).getDevCheckInfoList();

          assertThat(list)
              .isNotEmpty()
              .allSatisfy(info -> assertThat(info.urlList().get(0)).startsWith("/app/_check2/"));
        });
  }

  @Test
  void queryStartsWithQuestionMarkAfterPathVariable() throws NoSuchMethodException {
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest("GET", "/_check/index")));
    var method = UrlSample.class.getDeclaredMethod("find", String.class, String.class);

    assertThat(new DevCheckInfoMvcService().getUrlWithParameter("/_check/items/{id}", method))
        .isEqualTo("/_check/items/{id}?name={name}");
  }

  @Test
  void queryUsesRequestParamNamesAndSkipsNonQueryArguments() throws NoSuchMethodException {
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest("GET", "/_check/index")));
    var method =
        UrlSample.class.getDeclaredMethod(
            "search",
            String.class,
            HttpServletRequest.class,
            Locale.class,
            String.class,
            List.class,
            int.class);

    assertThat(new DevCheckInfoMvcService().getUrlWithParameter("/_check/search", method))
        .isEqualTo("/_check/search?q={q}&tags={tags}&page={page}");
  }

  @Test
  void listsJsonControllersWithCharset() {
    this.contextRunner
        .withUserConfiguration(CharsetController.class)
        .run(context -> {
          RequestContextHolder.setRequestAttributes(
              new ServletRequestAttributes(new MockHttpServletRequest("GET", "/_check/index")));

          assertThat(context.getBean(DevCheckInfoMvcService.class).getDevCheckInfoList())
              .anyMatch(info -> info.method().equals("charsetJson"));
        });
  }

  static class UrlSample {
    public void find(@PathVariable String id, String name) {
      // 시그니처만 쓴다 - 파라미터 이름과 @PathVariable 으로 URL 을 만드는지 본다
    }

    public void search(
        @RequestParam("q") String query,
        HttpServletRequest request,
        Locale locale,
        @RequestHeader String token,
        @RequestParam List<String> tags,
        int page) {
      // 시그니처만 쓴다 - 쿼리로 오지 않는 인자는 빠지고, @RequestParam 이름을 따르는지 본다
    }
  }

  @DevCheckController
  @RequestMapping(value = "/charset", produces = "application/json;charset=UTF-8")
  static class CharsetController {
    @GetMapping("/json")
    public String charsetJson() {
      return "{}";
    }
  }

  /** Boot 4.2 removed spring.mvc.pathmatch.matching-strategy=ant-path-matcher; apps still can. */
  @Configuration(proxyBeanMethods = false)
  static class AntPathMatcherConfiguration implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
      configurer.setPathMatcher(new AntPathMatcher());
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class SecondHandlerMappingConfiguration {

    /** dev-check 접두를 붙이는 같은 종류라 첫 mapping 과 키가 똑같이 나온다. */
    @Bean
    RequestMappingHandlerMapping secondRequestMappingHandlerMapping(
        WebMvcRegistrations webMvcRegistrations) {
      return webMvcRegistrations.getRequestMappingHandlerMapping();
    }
  }
}
