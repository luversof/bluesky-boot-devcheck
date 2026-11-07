package io.github.luversof.boot.autoconfigure.devcheck;

import org.aspectj.weaver.Advice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.luversof.boot.devcheck.DevCheckProperties;
import io.github.luversof.boot.devcheck.aspect.DevCheckControllerAspect;
import io.github.luversof.boot.devcheck.service.DevCheckUtilInfoService;

@AutoConfiguration
@EnableConfigurationProperties(DevCheckProperties.class)
@ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true")
public class DevCheckAutoConfiguration {

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(Advice.class)
  static class AspectJAutoProxyingConfiguration {

    @Bean
    DevCheckControllerAspect devCheckControllerAspect(DevCheckProperties devCheckProperties) {
      return new DevCheckControllerAspect(devCheckProperties);
    }
  }

  @Bean
  DevCheckUtilInfoService devCheckUtilInfoService(DevCheckProperties devCheckProperties) {
    return new DevCheckUtilInfoService(devCheckProperties);
  }
}
