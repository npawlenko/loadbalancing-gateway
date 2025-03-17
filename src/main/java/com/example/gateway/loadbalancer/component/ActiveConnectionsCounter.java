package com.example.gateway.loadbalancer.component;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "np.loadbalancer.strategy", havingValue = "LEAST_CONNECTIONS")
public class ActiveConnectionsCounter {

	private final Map<ServiceInstance, Integer> activeConnections = new ConcurrentHashMap<>();

	public void increment(ServiceInstance serviceInstance) {
		updateConnectionCount(serviceInstance, 1);
	}

	public void decrement(ServiceInstance serviceInstance) {
		updateConnectionCount(serviceInstance, -1);
	}

	private void updateConnectionCount(ServiceInstance serviceInstance, int delta) {
		activeConnections.compute(serviceInstance,
			(instance, count) -> Math.max(0, (count == null ? 0 : count) + delta));
	}

	public Map<ServiceInstance, Integer> getActiveConnectionsMap() {
		return activeConnections.entrySet().stream()
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(Map.Entry::getKey, Entry::getValue),
				Collections::unmodifiableMap
			));
	}
}
