package com.ibm.ibmid.resourceserver.config;

import com.ibm.ibmid.resourceserver.oauth2.decoder.IbmIdJwtDecoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Order(1)
public class IbmIdResourceServerWebSecurityConfig extends AbstractHttpConfigurer<IbmIdResourceServerWebSecurityConfig, HttpSecurity> {

    private final @NonNull IbmIdJwtDecoder ibmIdJwtDecoder;

    @Override
    public void init(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .oauth2ResourceServer()
                .jwt()
                .decoder(ibmIdJwtDecoder);

    }
}
