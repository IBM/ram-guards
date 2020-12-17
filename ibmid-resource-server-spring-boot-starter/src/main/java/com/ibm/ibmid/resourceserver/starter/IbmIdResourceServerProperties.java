package com.ibm.ibmid.resourceserver.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author seanyu
 */
@ConfigurationProperties(prefix = "ibm-id.resource-server")
@Data
@Component
public class IbmIdResourceServerProperties {
    private String jwkCertificatePath;
    private String clientId;
    private String clientSecret;
    private String tokenIntrospectionEndpoint;
    private List<String> systemPartners;
    private String jwkSetEndpoint;
}
