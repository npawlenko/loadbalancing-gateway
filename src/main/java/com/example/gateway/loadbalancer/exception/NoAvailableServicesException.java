package com.example.gateway.loadbalancer.exception;

public class NoAvailableServicesException extends RuntimeException {

	public NoAvailableServicesException(String serviceId) {
		super("No available service found for '" + serviceId + "'");
	}
}
