package com.ibm.ram.guards.resourceserver.config;

import com.ibm.ram.guards.resourceserver.oauth2.converter.RamGuardsJweAuthenticationConverter;
import com.ibm.ram.guards.resourceserver.oauth2.decoder.RamGuardsJweDecoder;
import com.ibm.ram.guards.resourceserver.web.filter.RamGuardsTokenUsernameFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsResourceServerWebSecurityConfig extends AbstractHttpConfigurer<RamGuardsResourceServerWebSecurityConfig, HttpSecurity> {

    private final @NonNull RamGuardsJweAuthenticationConverter ramGuardsJweAuthenticationConverter;

    private final @NonNull RamGuardsJweDecoder ramGuardsJweDecoder;

    @Override
    public void init(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .addFilterAfter(new RamGuardsTokenUsernameFilter(), BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer()
                .jwt()
                .decoder(ramGuardsJweDecoder)
                .jwtAuthenticationConverter(ramGuardsJweAuthenticationConverter);
    }
}
