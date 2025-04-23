package com.example.gateway.loadbalancer.strategy.impl;

import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategyWithFallback;
import com.example.gateway.utils.WebExchangeUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
@Slf4j
public class IpHashStrategy extends LoadBalancerStrategyWithFallback {

	@NonNull
	@Override
	public ServiceInstance selectInstance(@NonNull List<ServiceInstance> instances, Request<?> request) {
		String ip = WebExchangeUtils.getClientIp(request);
		if (ip == null) {
			log.debug("Could not determine client IP address");
			return selectInstanceFallback(instances, request);
		}
		int index = Math.abs(ip.hashCode() % instances.size());
		return instances.get(index);
	}
}
