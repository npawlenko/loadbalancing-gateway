package com.example.gateway.loadbalancer.strategy;

public enum LoadBalancerStrategyEnum {
	RANDOM,
	LEAST_CONNECTIONS,
	WEIGHTED_LEAST_CONNECTIONS,
	LEAST_RESPONSE_TIME,
	ROUND_ROBIN,
	WEIGHTED_ROUND_ROBIN,
	IP_HASH
}
