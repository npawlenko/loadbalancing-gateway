package com.example.gateway.loadbalancer.strategy;

import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.loadbalancer.strategy.impl.LeastConnectionsStrategy;
import com.example.gateway.loadbalancer.strategy.impl.RandomInstanceStrategy;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancerStrategyFactory {

	private final Map<LoadBalancerStrategyEnum, Supplier<LoadBalancerStrategy>> strategyMap;

	@Value("${loadbalancer.strategy}")
	private LoadBalancerStrategyEnum strategy;

	public LoadBalancerStrategyFactory(@Lazy ActiveConnectionsCounter activeConnectionsCounter) {
		this.strategyMap = Map.of(
			LoadBalancerStrategyEnum.RANDOM, RandomInstanceStrategy::new,
			LoadBalancerStrategyEnum.LEAST_CONNECTIONS, () -> new LeastConnectionsStrategy(activeConnectionsCounter));
	}

	public LoadBalancerStrategy getStrategy() {
		return Optional
			.ofNullable(strategy)
			.map(strategyMap::get)
			.map(Supplier::get)
			.orElseThrow(() -> new IllegalArgumentException(
				"Unsupported loadbalancer.strategy property value"));
	}
}
