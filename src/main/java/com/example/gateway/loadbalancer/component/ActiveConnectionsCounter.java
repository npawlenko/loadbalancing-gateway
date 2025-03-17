package com.example.gateway.loadbalancer.component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "np.loadbalancer.strategy", havingValue = "LEAST_CONNECTIONS")
public class ActiveConnectionsCounter {

	private final ConcurrentHashMap<ServiceInstance, AtomicInteger> activeConnections = new ConcurrentHashMap<>();

	public void increment(ServiceInstance serviceInstance) {
		getNumberOfActiveConnections(serviceInstance).incrementAndGet();
	}

	public void decrement(ServiceInstance serviceInstance) {
		getNumberOfActiveConnections(serviceInstance).decrementAndGet();
	}

	private AtomicInteger getNumberOfActiveConnections(ServiceInstance serviceInstance) {
		return activeConnections.computeIfAbsent(serviceInstance, k -> new AtomicInteger(0));
	}

	public Map<ServiceInstance, Integer> getActiveConnectionsMap() {
		return activeConnections.entrySet().stream()
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()),
				Collections::unmodifiableMap
			));
	}

}
