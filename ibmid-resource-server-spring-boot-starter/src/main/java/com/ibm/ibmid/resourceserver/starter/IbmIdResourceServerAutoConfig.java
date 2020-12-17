package com.ibm.ibmid.resourceserver.starter;


import com.ibm.ibmid.resourceserver.oauth2.converter.IbmIdTokenIntrospectionConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * @author seanyu
 */
@ConditionalOnClass({RemoteTokenServices.class})
@EnableConfigurationProperties({IbmIdResourceServerProperties.class})
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ComponentScan("com.ibm.ibmid.resourceserver")
public class IbmIdResourceServerAutoConfig {

    private final @NonNull IbmIdResourceServerProperties ibmIdResourceServerProperties;

    @Bean
    @ConditionalOnMissingBean(RemoteTokenServices.class)
    public RemoteTokenServices remoteTokenServices(){
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setClientId(ibmIdResourceServerProperties.getClientId());
        remoteTokenServices.setClientSecret(ibmIdResourceServerProperties.getClientSecret());
        remoteTokenServices.setCheckTokenEndpointUrl(ibmIdResourceServerProperties.getTokenIntrospectionEndpoint());
        remoteTokenServices.setAccessTokenConverter(new IbmIdTokenIntrospectionConverter());
        return remoteTokenServices;
    }
}
