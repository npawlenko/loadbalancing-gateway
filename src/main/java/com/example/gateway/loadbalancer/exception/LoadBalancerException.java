package com.example.gateway.loadbalancer.exception;

import lombok.Getter;

@Getter
public class LoadBalancerException extends RuntimeException {

	public LoadBalancerException(String message) {
		super(message);
	}
}
