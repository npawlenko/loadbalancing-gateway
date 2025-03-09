package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import java.util.List;
import java.util.Random;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.lang.NonNull;

public class RandomInstanceStrategy implements LoadBalancerStrategy {

	private final Random random = new Random();

	@NonNull
	@Override
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances) {
		int randomIndex = random.nextInt(instances.size());
		return instances.get(randomIndex);
	}
}
