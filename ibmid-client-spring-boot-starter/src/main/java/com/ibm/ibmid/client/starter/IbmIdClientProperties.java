package com.ibm.ibmid.client.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author seanyu
 */
@ConfigurationProperties(prefix = "ibm-id.client")
@Data
@Component
public class IbmIdClientProperties {
    private String clientId;
    private String clientSecret;
    private String authorizationUri;
    private String tokenUri;
    private String redirectUri;
    private String jwkCertificatePath;
    private String jwkSetUri;
}
