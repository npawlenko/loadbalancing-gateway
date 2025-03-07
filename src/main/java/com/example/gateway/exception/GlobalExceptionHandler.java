package com.example.gateway.exception;

import com.example.gateway.loadbalancer.exception.LoadBalancerException;
import com.example.gateway.loadbalancer.exception.NoAvailableServicesException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoAvailableServicesException.class)
	public ErrorResponse handleNoAvailableServicesException(NoAvailableServicesException e) {
		return getErrorResponse(e);
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(LoadBalancerException.class)
	public ErrorResponse handleLoadBalancerException(LoadBalancerException e) {
		return getErrorResponse(e);
	}

	private ErrorResponse getErrorResponse(Exception e) {
		return new ErrorResponse(LocalDateTime.now(), e.getMessage());
	}
}
