package com.example.gateway.loadbalancer.strategy;

public enum LoadBalancerStrategyEnum {
	RANDOM,
	LEAST_CONNECTIONS,
	ROUND_ROBIN,
	WEIGHTED_ROUND_ROBIN
}
