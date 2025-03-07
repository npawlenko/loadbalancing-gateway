package com.example.gateway.config;

import com.example.gateway.filter.DynamicRoutingFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
		DynamicRoutingFilter dynamicRoutingFilter) {
		return builder.routes()
			.route("any-service", r -> r
				.path("/{serviceId}/**")
				.filters(f -> f
					.stripPrefix(1)
					.filter(dynamicRoutingFilter)
				)
				.uri("lb://dummy")) // Placeholder, dynamically changed in filter
			.build();
	}

}
