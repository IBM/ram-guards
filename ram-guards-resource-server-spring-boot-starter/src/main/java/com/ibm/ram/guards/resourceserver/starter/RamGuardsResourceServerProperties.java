package com.ibm.ram.guards.resourceserver.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author seanyu
 */
@ConfigurationProperties(prefix = "ram-guards.resource-server")
@Data
public class RamGuardsResourceServerProperties {
    private String clientId;
    private String clientSecret;
    private List<String> systemPartners;
}
