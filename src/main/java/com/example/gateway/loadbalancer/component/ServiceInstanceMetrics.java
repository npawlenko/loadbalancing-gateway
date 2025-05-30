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

	private static final String MEASUREMENTS_STATISTIC = "statistic";
	private static final String MEASUREMENTS_VALUE = "value";
	private static final String MEASUREMENTS_STATISTIC_COUNT = "COUNT";
	private static final String MEASUREMENTS_STATISTIC_TOTAL_TIME = "TOTAL_TIME";

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
		String url = getInstanceUrl(instance);

		//noinspection unchecked
		return webClient.get()
			.uri(url)
			.retrieve()
			.bodyToMono(Map.class)
			.timeout(Duration.ofSeconds(2))
			.doOnNext(metrics -> updateResponseTime(instance, metrics))
			.doOnError(e -> log.error("Failed to fetch metrics from {} ({})\n{}", instance.getInstanceId(),
				url, e.getStackTrace()))
			.onErrorResume(e -> Mono.empty())
			.then();
	}

	private String getInstanceUrl(ServiceInstance instance) {
		String scheme = instance.getScheme() != null ? instance.getScheme() : "http";
		int port = instance.getPort();
		return scheme + "://" + instance.getHost() + ":" + port
			+ "/actuator/metrics/http.server.requests";
	}

	private void updateResponseTime(ServiceInstance instance, Map<String, Object> metrics) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> measurements = (List<Map<String, Object>>) metrics.get(
			"measurements");
		Double count = getMeasurement(MEASUREMENTS_STATISTIC_COUNT, measurements);
		Double totalTime = getMeasurement(MEASUREMENTS_STATISTIC_TOTAL_TIME, measurements);
		Double avgTime = totalTime / count;
		responseTimes.put(instance, avgTime);
	}

	private <T> T getMeasurement(String statistic,
		List<Map<String, Object>> measurements) {
		if (measurements == null || measurements.isEmpty()) {
			throw new IllegalStateException(
				"Could not fetch measurements statistic '" + statistic + "'");
		}
		//noinspection unchecked
		return measurements.stream().filter(m -> m.get(MEASUREMENTS_STATISTIC).equals(statistic))
			.findFirst()
			.map(e -> e.get(MEASUREMENTS_VALUE)).map(e -> (T) e)
			.orElseThrow(() -> new IllegalStateException(
				"Could not fetch measurements statistic '" + statistic + "'"));
	}
}
