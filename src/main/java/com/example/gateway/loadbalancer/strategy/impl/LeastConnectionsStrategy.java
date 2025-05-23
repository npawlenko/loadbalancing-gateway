package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategyWithFallback;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class LeastConnectionsStrategy extends LoadBalancerStrategyWithFallback {

	private final ActiveConnectionsCounter activeConnectionsCounter;

	@NonNull
	@Override
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances,
		Request<?> request) {
		return instances.stream()
			.min(Comparator.comparingInt(
				instance -> activeConnectionsCounter.getActiveConnectionsMap()
					.getOrDefault(instance, 0)))
			.orElseGet(() -> selectInstanceFallback(instances, request));
	}
}
