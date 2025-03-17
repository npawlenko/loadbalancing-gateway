package com.example.gateway.config;

import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalancerConfig {

	@Bean
	public LoadBalancerStrategy loadBalancerStrategy(LoadBalancerStrategyFactory strategyFactory) {
		return strategyFactory.createStrategyAndGet();
	}
}
