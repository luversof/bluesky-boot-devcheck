package io.github.luversof.boot.autoconfigure.devcheck.servlet;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckApiController;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckViewController;
import io.github.luversof.boot.devcheck.controller.DevCheckCoreController;
import io.github.luversof.boot.devcheck.controller.DevCheckCoreViewController;
import io.github.luversof.boot.devcheck.controller.servlet.DevCheckApiMvcController;
import io.github.luversof.boot.devcheck.service.DevCheckUtilInfoService;
import io.github.luversof.boot.devcheck.service.servlet.DevCheckInfoMvcService;
import jakarta.servlet.Servlet;

@AutoConfiguration
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DevCheckMvcAutoConfiguration implements WebMvcConfigurer {

	@Value("${bluesky-boot.dev-check.path-prefixes}")
	private String[] pathPrefixes;

	@Bean
	DevCheckInfoMvcService blueskyBootDevCheckInfoMvcService() {
		return new DevCheckInfoMvcService();
	}

	@Bean
	DevCheckApiMvcController blueskyBootDevCheckApiMvcController(DevCheckInfoMvcService devCheckInfoMvcService,DevCheckUtilInfoService devCheckUtilInfoService) {
		return new DevCheckApiMvcController(devCheckInfoMvcService, devCheckUtilInfoService);
	}

	@Bean
	DevCheckCoreViewController blueskyBootDevCheckCoreViewController() {
		return new DevCheckCoreViewController();
	}

	@Bean
	DevCheckCoreController blueskyBootDevCheckCoreController(ApplicationContext applicationContext) {
		return new DevCheckCoreController(applicationContext);
	}

	@Bean
	WebMvcRegistrations blueskyBootDevCheckwebMvcRegistrations() {
		return new WebMvcRegistrations() {
			@Override
			public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
				return new RequestMappingHandlerMapping() {

					@Override
					protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
						RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
						if (mappingInfo != null 
							&& (
								handlerType.isAnnotationPresent(DevCheckController.class)
								|| handlerType.isAnnotationPresent(DevCheckApiController.class)
								|| handlerType.isAnnotationPresent(DevCheckViewController.class)
							)
						) {
							return RequestMappingInfo.paths(pathPrefixes).build().combine(mappingInfo);
						}
						return mappingInfo;
					}

				};
			}
		};
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		for (var pathPrefix : pathPrefixes) {
			registry.addRedirectViewController(pathPrefix, pathPrefix + "/index");
		}
	}

}
