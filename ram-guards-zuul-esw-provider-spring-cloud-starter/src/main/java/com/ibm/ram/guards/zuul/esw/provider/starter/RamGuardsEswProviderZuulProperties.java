package com.ibm.ram.guards.zuul.esw.provider.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author seanyu
 */
@ConfigurationProperties(prefix = "ram-guards.zuul")
@Data
public class RamGuardsEswProviderZuulProperties {
    private String authorizationServerServiceId;
}
