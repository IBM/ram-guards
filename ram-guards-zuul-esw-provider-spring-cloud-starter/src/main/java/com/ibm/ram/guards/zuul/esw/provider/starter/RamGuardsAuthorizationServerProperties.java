package com.ibm.ram.guards.zuul.esw.provider.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author seanyu
 */
@ConfigurationProperties(prefix = "ram-guards.authorization-server")
@Data
public class RamGuardsAuthorizationServerProperties {
    private String clientId;
    private String clientSecret;
    private String defaultPassword;
}
