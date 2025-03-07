package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class LeastConnectionsStrategy implements LoadBalancerStrategy {

	private final ActiveConnectionsCounter activeConnectionsCounter;

	@NonNull
	@Override
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances) {
		return activeConnectionsCounter.getActiveConnectionsMap()
			.entrySet()
			.stream()
			.min(Comparator.comparingInt(Entry::getValue))
			.map(Entry::getKey)
			.orElse(instances.getFirst());
	}
}
