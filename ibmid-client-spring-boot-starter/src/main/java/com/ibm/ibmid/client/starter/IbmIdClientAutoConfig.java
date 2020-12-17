package com.ibm.ibmid.client.starter;

import com.ibm.ibmid.client.oauth2.IbmIdJwtDecoder;
import com.ibm.ibmid.client.oauth2.OpenIdConnectAuthenticationFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@EnableConfigurationProperties({IbmIdClientProperties.class})
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ComponentScan("com.ibm.ibmid.client")
@EnableOAuth2Client
public class IbmIdClientAutoConfig {

    private final @NonNull IbmIdClientProperties ibmIdClientProperties;

    @Bean
    @ConditionalOnMissingBean({OAuth2ProtectedResourceDetails.class})
    public OAuth2ProtectedResourceDetails createOpenIdConnectConfig() {
        final AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
        resourceDetails.setClientId(ibmIdClientProperties.getClientId());
        resourceDetails.setClientSecret(ibmIdClientProperties.getClientSecret());
        resourceDetails.setUserAuthorizationUri(ibmIdClientProperties.getAuthorizationUri());
        resourceDetails.setAccessTokenUri(ibmIdClientProperties.getTokenUri());
        final List<String> scopes = Collections.singletonList("openid");
        resourceDetails.setScope(scopes);
        resourceDetails.setPreEstablishedRedirectUri(ibmIdClientProperties.getRedirectUri());
        resourceDetails.setUseCurrentUri(false);
        return resourceDetails;
    }

    @Bean
    @ConditionalOnMissingBean({OAuth2RestTemplate.class})
    public OAuth2RestTemplate getOpenIdConnectRestTemplate(OAuth2ClientContext clientContext, OAuth2ProtectedResourceDetails createOpenIdConnectConfig) {
        return new OAuth2RestTemplate(createOpenIdConnectConfig, clientContext);
    }

    @Bean
    @ConditionalOnMissingBean({OpenIdConnectAuthenticationFilter.class})
    public OpenIdConnectAuthenticationFilter createOpenIdConnectFilter(OAuth2RestTemplate oAuth2RestTemplate, IbmIdJwtDecoder ibmIdJwtDecoder) throws URISyntaxException {
        URI uri = new URI(ibmIdClientProperties.getRedirectUri());
        OpenIdConnectAuthenticationFilter openIdConnectFilter = new OpenIdConnectAuthenticationFilter(uri.getPath());
        openIdConnectFilter.setOauth2RestTemplate(oAuth2RestTemplate);
        openIdConnectFilter.setIbmIdJwtDecoder(ibmIdJwtDecoder);
        return openIdConnectFilter;
    }

}
