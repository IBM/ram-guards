package com.ibm.ram.guards;

import com.ibm.ibmid.resourceserver.config.IbmIdResourceServerWebSecurityConfig;
import com.ibm.ibmid.resourceserver.oauth2.decoder.IbmIdJwtDecoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * @author seanyu
 */
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final @NonNull IbmIdResourceServerWebSecurityConfig ibmIdResourceServerWebSecurityConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .apply(ibmIdResourceServerWebSecurityConfig)
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .mvcMatchers("/error");
    }
}

