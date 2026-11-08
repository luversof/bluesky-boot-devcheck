package io.github.luversof.boot.autoconfigure.devcheck;

import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_CORE_SERVLET_USER_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.devcheck.AutoConfigurationTestInfo.DEVCHECK_SERVLET_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.devcheck.service.servlet.DevCheckInfoMvcService;

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
        .withPropertyValues("spring.mvc.pathmatch.matching-strategy=ant-path-matcher")
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
  void queryStartsWithQuestionMarkAfterPathVariable() throws NoSuchMethodException {
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest("GET", "/_check/index")));
    var method = UrlSample.class.getDeclaredMethod("find", String.class, String.class);

    assertThat(new DevCheckInfoMvcService().getUrlWithParameter("/_check/items/{id}", method))
        .isEqualTo("/_check/items/{id}?name={name}");
  }

  static class UrlSample {
    public void find(@PathVariable String id, String name) {
      // 시그니처만 쓴다 - 파라미터 이름과 @PathVariable 으로 URL 을 만드는지 본다
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
