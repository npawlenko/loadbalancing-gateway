package com.example.gateway.loadbalancer.strategy;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
@Slf4j
public abstract class LoadBalancerStrategyWithFallback implements LoadBalancerStrategy {

	private Random random;

	protected ServiceInstance selectInstanceFallback(@NonNull List<ServiceInstance> instances, Request<?> request) {
		log.debug("Could not get instance using strategy. Using fallback method");
		if (random == null) {
			random = new Random();
		}
		int randomIndex = new Random().nextInt(instances.size());
		return instances.get(randomIndex);
	}
}
