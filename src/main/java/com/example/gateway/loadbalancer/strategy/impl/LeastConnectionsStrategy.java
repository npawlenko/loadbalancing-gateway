package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class LeastConnectionsStrategy implements LoadBalancerStrategy {

	private final ActiveConnectionsCounter activeConnectionsCounter;

	@NonNull
	@Override
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances) {
		return instances.stream()
			.min(Comparator.comparingInt(
				instance -> activeConnectionsCounter.getActiveConnectionsMap()
					.getOrDefault(instance, 0)))
			.orElse(instances.getFirst());
	}
}
