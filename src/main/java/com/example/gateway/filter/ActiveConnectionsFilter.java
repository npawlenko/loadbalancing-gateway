package com.example.gateway.filter;

import com.example.gateway.loadbalancer.ServiceInstanceResponse;
import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActiveConnectionsFilter implements GlobalFilter, Ordered {

	private static final String LOADBALANCER_SERVER_INSTANCE_RESPONSE_ATTRIBUTE = "org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayLoadBalancerResponse";

	private final ActiveConnectionsCounter activeConnectionsCounter;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServiceInstanceResponse serviceInstanceResponse = exchange.getAttribute(
			LOADBALANCER_SERVER_INSTANCE_RESPONSE_ATTRIBUTE);
		if (serviceInstanceResponse == null || !serviceInstanceResponse.hasServer()) {
			log.warn("ServiceInstanceResponse was not set. ActiveConnectionsFilter might not work correctly");
			return chain.filter(exchange);
		}
		ServiceInstance serviceInstance = serviceInstanceResponse.getServer();
		activeConnectionsCounter.increment(serviceInstance);
		return chain.filter(exchange)
			.doFinally(signalType -> activeConnectionsCounter.decrement(serviceInstance));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
