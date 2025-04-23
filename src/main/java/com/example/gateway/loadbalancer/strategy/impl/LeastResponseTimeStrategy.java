package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.component.ServiceInstanceMetrics;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategyWithFallback;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
@Slf4j
public class LeastResponseTimeStrategy extends LoadBalancerStrategyWithFallback {

	private final ServiceInstanceMetrics serviceInstanceMetrics;

	@Override
	@NonNull
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances, Request<?> request) {
		return instances.stream()
			.map(instance -> Map.entry(instance, serviceInstanceMetrics.getResponseTime(instance)))
			.min(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElseGet(() -> {
				log.warn("No metrics found");
				return selectInstanceFallback(instances, request);
			});
	}

}
