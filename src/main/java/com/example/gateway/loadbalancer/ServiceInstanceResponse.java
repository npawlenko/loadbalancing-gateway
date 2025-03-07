package com.example.gateway.loadbalancer;


import java.util.Objects;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;

public record ServiceInstanceResponse(ServiceInstance instance) implements
	Response<ServiceInstance> {

	@Override
	public boolean hasServer() {
		return instance != null;
	}

	@Override
	public ServiceInstance getServer() {
		return instance;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceInstanceResponse(ServiceInstance otherInstance)) {
			return Objects.equals(instance, otherInstance);
		}
		return false;
	}
}
