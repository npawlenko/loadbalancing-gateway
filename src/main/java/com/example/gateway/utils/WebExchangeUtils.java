package com.example.gateway.utils;

import com.example.gateway.config.RoutesConfig;
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
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class WebExchangeUtils {

	public String getServiceId(Request<?> request) {
		return getRequestData(request).map(WebExchangeUtils::getServiceId).orElse(null);
	}

	public String getServiceId(RequestData requestData) {
		@SuppressWarnings("unchecked")
		Map<String, String> uriTemplateVariables = (Map<String, String>) requestData.getAttributes()
			.get(ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		return getServiceId(uriTemplateVariables);
	}

	private String getServiceId(Map<String, String> uriTemplateVariables) {
		return Optional
			.ofNullable(uriTemplateVariables)
			.map(m -> m.get(RoutesConfig.PARAM_SERVICE_ID))
			.orElseThrow(() -> new IllegalStateException("No service id set"));
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
