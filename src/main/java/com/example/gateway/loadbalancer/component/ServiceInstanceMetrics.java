package com.example.gateway.loadbalancer.component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceInstanceMetrics {

	private final ReactiveDiscoveryClient reactiveDiscoveryClient;

	private final WebClient webClient = WebClient.create();
	private final Map<ServiceInstance, Double> responseTimes = new ConcurrentHashMap<>();

	public double getResponseTime(ServiceInstance instance) {
		return responseTimes.getOrDefault(instance, 0D);
	}

	@Scheduled(fixedRate = 10_000)
	public void updateMetrics() {
		reactiveDiscoveryClient.getServices()
			.flatMap(reactiveDiscoveryClient::getInstances)
			.flatMap(this::fetchResponseTime)
			.subscribe();
	}

	private Mono<Void> fetchResponseTime(ServiceInstance instance) {
		String url = instance.getUri() + "/actuator/metrics/http.server.requests";

		//noinspection unchecked
		return webClient.get()
			.uri(url)
			.retrieve()
			.bodyToMono(Map.class)
			.timeout(Duration.ofSeconds(2))
			.doOnNext(metrics -> updateResponseTime(instance, metrics))
			.doOnError(e -> log.error("Failed to fetch metrics from {}:{}", instance.getServiceId(),
				instance.getInstanceId()))
			.onErrorResume(e -> Mono.empty())
			.then();
	}

	private void updateResponseTime(ServiceInstance instance, Map<String, Object> metrics) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> measurements = (List<Map<String, Object>>) metrics.get(
			"measurements");
		Double count = getMeasurement("COUNT", measurements);
		Double totalTime = getMeasurement("TOTAL_TIME", measurements);
		Double avgTime = totalTime / count;
		responseTimes.put(instance, avgTime);
	}

	private <T> T getMeasurement(String statistic,
		List<Map<String, Object>> measurements) {
		if (measurements == null || measurements.isEmpty()) {
			throw new IllegalStateException(
				"Could not fetch measurements statistic '" + statistic + "'");
		}
		return measurements.stream().filter(m -> m.get("statistic").equals(statistic)).findFirst()
			.map(e -> e.get("value")).map(e -> (T) e).orElseThrow(() -> new IllegalStateException(
				"Could not fetch measurements statistic '" + statistic + "'"));
	}
}
