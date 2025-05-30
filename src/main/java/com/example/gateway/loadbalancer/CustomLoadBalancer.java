package com.example.gateway.loadbalancer;


import com.example.gateway.loadbalancer.exception.LoadBalancerException;
import com.example.gateway.loadbalancer.exception.NoAvailableServicesException;
import com.example.gateway.loadbalancer.strategy.LoadBalancerStrategy;
import com.example.gateway.utils.WebExchangeUtils;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {

	private final LoadBalancerClientFactory clientFactory;
	private final LoadBalancerStrategy strategy;

	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		String serviceId = WebExchangeUtils.extractServiceId(request);

		if (serviceId == null) {
			throw new LoadBalancerException("No service parameter provided");
		}

		return getServiceInstanceList(request, serviceId)
			.map(instances -> chooseInstance(serviceId, instances, request))
			.map(ServiceInstanceResponse::new);
	}

	private Mono<List<ServiceInstance>> getServiceInstanceList(Request<?> request,
		String serviceId) {
		return Objects.requireNonNull(clientFactory
				.getProvider(
					serviceId, ServiceInstanceListSupplier.class)
				.getIfAvailable())
			.get(request)
			.next();
	}

	private ServiceInstance chooseInstance(String serviceId, List<ServiceInstance> instances,
		Request<?> request) {
		if (instances == null || instances.isEmpty()) {
			throw new NoAvailableServicesException(serviceId);
		}
		if (instances.size() == 1) {
			return instances.getFirst();
		}
		return strategy.selectInstance(instances, request);
	}
}