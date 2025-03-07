package com.example.gateway.filter;

import java.net.URI;
import java.util.LinkedHashSet;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class DynamicRoutingFilter implements GatewayFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		@SuppressWarnings("unchecked")
		URI path = ((LinkedHashSet<URI>) exchange.getRequiredAttribute(
			ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR)).getFirst();
		String[] pathSegments = path.getPath().split("/");

		if (pathSegments.length < 2) {
			return chain.filter(exchange);
		}

		String serviceId = pathSegments[1];
		URI newUri = URI.create("lb://" + serviceId);
		exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newUri);

		return chain.filter(exchange);
	}
}