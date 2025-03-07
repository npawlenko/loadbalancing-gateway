package com.example.gateway.loadbalancer.strategy;

import java.util.List;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.lang.NonNull;

public interface LoadBalancerStrategy {

	@NonNull
	ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances);
}
