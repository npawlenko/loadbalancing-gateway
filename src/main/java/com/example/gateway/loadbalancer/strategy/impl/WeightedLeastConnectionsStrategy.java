package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.config.properties.LoadBalancerProperties;
import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategyWithFallback;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class WeightedLeastConnectionsStrategy extends LoadBalancerStrategyWithFallback {

	private final LoadBalancerProperties loadBalancerProperties;
	private final ActiveConnectionsCounter activeConnectionsCounter;

	@NonNull
	@Override
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances,
		Request<?> request) {
		return instances.stream()
			.min(Comparator.comparingDouble(instance ->
				getActiveConnections(instance)
					/ (double) getWeight(instance)))
			.orElseGet(() -> selectInstanceFallback(instances, request));
	}

	private Integer getActiveConnections(ServiceInstance instance) {
		return activeConnectionsCounter.getActiveConnectionsMap().getOrDefault(instance, 0);
	}

	private Integer getWeight(ServiceInstance instance) {
		return loadBalancerProperties.getWeights()
			.getOrDefault(instance.getInstanceId(), 1);
	}
}
