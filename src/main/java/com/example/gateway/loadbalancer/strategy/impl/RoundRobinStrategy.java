package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.lang.NonNull;

public class RoundRobinStrategy implements LoadBalancerStrategy {

	private final AtomicInteger currentInstanceIndex = new AtomicInteger(0);

	@Override
	@NonNull
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances) {
		int index = currentInstanceIndex.getAndIncrement();
		if (index >= instances.size()) {
			currentInstanceIndex.set(0);
		}
		return instances.get(index);
	}
}
