package com.example.gateway.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequestContext;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class WebExchangeUtils {

	public String extractServiceId(Request<?> request) {
		return getRequestData(request)
			.map(requestData -> {
				Route route = (Route) requestData.getAttributes()
					.get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

				if (route != null && route.getUri() != null) {
					String uri = route.getUri().toString();
					if (uri.startsWith("lb://")) {
						return uri.substring(5);
					}
				}
				return null;
			})
			.orElse(null);
	}

	public String getClientIp(Request<?> request) {
		return getRequestData(request).map(WebExchangeUtils::getClientIp).orElse(null);
	}

	private String getClientIp(RequestData requestData) {
		ServerWebExchange exchange = (ServerWebExchange) requestData.getAttributes()
			.get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
		if (exchange != null) {
			String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
			if (ip == null || ip.isEmpty()) {
				ip = Optional.of(exchange.getRequest()).map(ServerHttpRequest::getRemoteAddress)
					.map(
						InetSocketAddress::getAddress).map(InetAddress::getHostAddress)
					.orElse(null);
			}
			return ip;
		}
		return null;
	}

	public Response<ServiceInstance> getLoadbalancerServiceInstanceResponseAttribute(
		ServerWebExchange exchange) {
		return exchange.getRequiredAttribute(
			ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR);
	}

	private Optional<RequestData> getRequestData(Request<?> request) {
		if (request.getContext() instanceof DefaultRequestContext context) {
			Object clientRequest = context.getClientRequest();
			if (clientRequest instanceof RequestData requestData) {
				return Optional.of(requestData);
			}
		}
		return Optional.empty();
	}
}
