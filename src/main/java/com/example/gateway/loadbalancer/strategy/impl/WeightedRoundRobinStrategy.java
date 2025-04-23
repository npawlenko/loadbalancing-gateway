package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.config.properties.LoadBalancerProperties;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class WeightedRoundRobinStrategy implements LoadBalancerStrategy {

	private final LoadBalancerProperties loadBalancerProperties;
	private final AtomicInteger currentInstanceIndex = new AtomicInteger(0);
	private final AtomicInteger currentInstanceWeight = new AtomicInteger(0);

	@Override
	@NonNull
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances,
		Request<?> request) {
		int weight = currentInstanceWeight.get();
		if (weight > 0 && currentInstanceWeight.compareAndSet(weight, weight - 1)) {
			return instances.get(currentInstanceIndex.get());
		}
		if (currentInstanceWeight.compareAndSet(0, -1)) {
			return selectNextInstance(instances);
		}
		return instances.get(currentInstanceIndex.get());
	}

	private ServiceInstance selectNextInstance(List<ServiceInstance> instances) {
		int nextIndex = currentInstanceIndex.updateAndGet(i -> (i + 1) % instances.size());
		refreshCurrentInstanceWeight(instances.get(nextIndex));
		return instances.get(nextIndex);
	}

	private void refreshCurrentInstanceWeight(ServiceInstance instance) {
		String instanceId = instance.getInstanceId();
		int weight = loadBalancerProperties.getWeights().getOrDefault(instanceId, 1);
		currentInstanceWeight.set(weight);
	}
}
