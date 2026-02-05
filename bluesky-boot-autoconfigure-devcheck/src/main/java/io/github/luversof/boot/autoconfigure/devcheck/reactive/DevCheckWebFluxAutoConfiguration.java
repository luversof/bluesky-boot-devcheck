package io.github.luversof.boot.autoconfigure.devcheck.reactive;

import java.lang.reflect.Method;
import java.net.URI;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.webflux.autoconfigure.WebFluxRegistrations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckApiController;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckViewController;
import io.github.luversof.boot.devcheck.controller.DevCheckCoreController;
import io.github.luversof.boot.devcheck.controller.DevCheckCoreViewController;
import io.github.luversof.boot.devcheck.controller.reactive.DevCheckApiWebFluxController;
import io.github.luversof.boot.devcheck.service.DevCheckUtilInfoService;
import io.github.luversof.boot.devcheck.service.reactive.DevCheckInfoWebFluxService;

@AutoConfiguration
@ConditionalOnClass({ WebFluxConfigurer.class })
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DevCheckWebFluxAutoConfiguration {

	@Value("${bluesky-boot.dev-check.path-prefixes}")
	private String[] pathPrefixes;

	@Bean
	DevCheckInfoWebFluxService blueskyBootDevCheckInfoWebFluxService() {
		return new DevCheckInfoWebFluxService();
	}

	@Bean
	DevCheckApiWebFluxController blueskyBootDevCheckApiWebFluxController(
			DevCheckInfoWebFluxService devCheckInfoWebFluxService, DevCheckUtilInfoService devCheckUtilInfoService) {
		return new DevCheckApiWebFluxController(devCheckInfoWebFluxService, devCheckUtilInfoService);
	}

	@Bean
	DevCheckCoreViewController blueskyBootDevCheckViewController() {
		return new DevCheckCoreViewController();
	}

	@Bean
	DevCheckCoreController blueskyBootDevCheckCoreController(ApplicationContext applicationContext) {
		return new DevCheckCoreController(applicationContext);
	}

	@Bean
	WebFluxRegistrations blueskyBootDevCheckWebFluxRegistrations() {
		return new WebFluxRegistrations() {

			@Override
			public @NonNull RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
				return new RequestMappingHandlerMapping() {

					@Override
					protected @NonNull RequestMappingInfo getMappingForMethod(@NonNull Method method, @NonNull Class<?> handlerType) {
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
	
	@Bean
	RouterFunction<ServerResponse> blueskyBootDevCheckViewRouterFunction() {
		var builder = RouterFunctions.route();
		for (var pathPrefix : pathPrefixes) {
			builder.route(RequestPredicates.GET(pathPrefix), request -> ServerResponse.temporaryRedirect(URI.create(pathPrefix + "/index")).build());
		}
		return builder.build();
	}

}
