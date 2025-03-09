package com.example.gateway.utils;

import com.example.gateway.config.RoutesConfig;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class WebExchangeUtils {

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

	public Response<ServiceInstance> getLoadbalancerServiceInstanceResponseAttribute(
		ServerWebExchange exchange) {
		return exchange.getRequiredAttribute(
			ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR);
	}
}
