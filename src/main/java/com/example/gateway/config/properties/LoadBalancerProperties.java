package com.example.gateway.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "np.loadbalancer")
@Getter
@Setter
public class LoadBalancerProperties {

	private Map<String, Integer> weights;
}
