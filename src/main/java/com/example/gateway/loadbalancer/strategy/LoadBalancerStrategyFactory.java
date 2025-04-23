package com.example.gateway.loadbalancer.strategy;

import com.example.gateway.config.properties.LoadBalancerProperties;
import com.example.gateway.loadbalancer.component.ActiveConnectionsCounter;
import com.example.gateway.loadbalancer.component.ServiceInstanceMetrics;
import com.example.gateway.loadbalancer.strategy.impl.IpHashStrategy;
import com.example.gateway.loadbalancer.strategy.impl.LeastConnectionsStrategy;
import com.example.gateway.loadbalancer.strategy.impl.LeastResponseTimeStrategy;
import com.example.gateway.loadbalancer.strategy.impl.RandomInstanceStrategy;
import com.example.gateway.loadbalancer.strategy.impl.RoundRobinStrategy;
import com.example.gateway.loadbalancer.strategy.impl.WeightedLeastConnectionsStrategy;
import com.example.gateway.loadbalancer.strategy.impl.WeightedRoundRobinStrategy;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancerStrategyFactory {

	private final Map<LoadBalancerStrategyEnum, Supplier<LoadBalancerStrategy>> strategyMap;

	@Value("${np.loadbalancer.strategy}")
	private LoadBalancerStrategyEnum strategy;

	public LoadBalancerStrategyFactory(@Lazy ActiveConnectionsCounter activeConnectionsCounter,
		@Lazy ServiceInstanceMetrics serviceInstanceMetrics,
		@Lazy LoadBalancerProperties loadBalancerProperties) {
		this.strategyMap = Map.of(
			LoadBalancerStrategyEnum.RANDOM, RandomInstanceStrategy::new,
			LoadBalancerStrategyEnum.LEAST_CONNECTIONS, () -> new LeastConnectionsStrategy(activeConnectionsCounter),
			LoadBalancerStrategyEnum.WEIGHTED_LEAST_CONNECTIONS, () -> new WeightedLeastConnectionsStrategy(loadBalancerProperties, activeConnectionsCounter),
			LoadBalancerStrategyEnum.LEAST_RESPONSE_TIME, () -> new LeastResponseTimeStrategy(serviceInstanceMetrics),
			LoadBalancerStrategyEnum.ROUND_ROBIN, RoundRobinStrategy::new,
			LoadBalancerStrategyEnum.WEIGHTED_ROUND_ROBIN, () -> new WeightedRoundRobinStrategy(loadBalancerProperties),
			LoadBalancerStrategyEnum.IP_HASH, IpHashStrategy::new);
	}

	public LoadBalancerStrategy createStrategyAndGet() {
		return Optional
			.ofNullable(strategy)
			.map(strategyMap::get)
			.map(Supplier::get)
			.orElseThrow(() -> new IllegalArgumentException(
				"Unsupported loadbalancer.strategy property value"));
	}
}
