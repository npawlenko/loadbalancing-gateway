package com.example.gateway.filter;

import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.utils.WebExchangeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "loadbalancer.strategy", havingValue = "LEAST_CONNECTIONS")
@Slf4j
@RequiredArgsConstructor
public class ActiveConnectionsFilter implements GlobalFilter, Ordered {

	private final ActiveConnectionsCounter activeConnectionsCounter;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		Response<ServiceInstance> serviceInstanceResponse = WebExchangeUtils.getLoadbalancerServiceInstanceResponseAttribute(
			exchange);
		if (serviceInstanceResponse == null || !serviceInstanceResponse.hasServer()) {
			log.warn(
				"ServiceInstanceResponse was not set. ActiveConnectionsFilter might not work correctly");
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
