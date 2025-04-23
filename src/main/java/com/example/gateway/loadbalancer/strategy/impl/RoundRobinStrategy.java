package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

public class RoundRobinStrategy implements LoadBalancerStrategy {

	private final AtomicInteger currentInstanceIndex = new AtomicInteger(0);

	@Override
	@NonNull
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances, Request<?> request) {
		int index = currentInstanceIndex.getAndUpdate(i -> (i + 1) % instances.size());
		return instances.get(index);
	}
}
