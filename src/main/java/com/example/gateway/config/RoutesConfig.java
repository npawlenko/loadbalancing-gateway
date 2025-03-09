package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

	public static final String PARAM_SERVICE_ID = "serviceId";

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("any-service", r -> r
				.path("/{" + PARAM_SERVICE_ID + "}/**")
				.filters(f -> f
					.stripPrefix(1)
				)
				.uri("lb://dummy")) // Placeholder
			.build();
	}

}
