package com.ibm.ibmid.client.config;

import com.ibm.ibmid.client.oauth2.OpenIdConnectAuthenticationFilter;
import com.ibm.ibmid.client.starter.IbmIdClientProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import java.net.URI;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IbmIdClientWebSecurityConfig extends AbstractHttpConfigurer<IbmIdClientWebSecurityConfig, HttpSecurity> {

    private final @NonNull OpenIdConnectAuthenticationFilter openIdConnectAuthenticationFilter;

    private final @NonNull IbmIdClientProperties ibmIdClientProperties;

    private final @NonNull OAuth2ClientContextFilter oAuth2ClientContextFilter;

    @Override
    public void init(HttpSecurity httpSecurity) throws Exception {
        URI uri = new URI(ibmIdClientProperties.getRedirectUri());
        HttpBasicConfigurer httpBasicConfigurer = new HttpBasicConfigurer();
        httpBasicConfigurer.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(uri.getPath()));
        httpSecurity.apply(httpBasicConfigurer);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .addFilterAfter(oAuth2ClientContextFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(openIdConnectAuthenticationFilter, OAuth2ClientContextFilter.class);

    }
}
