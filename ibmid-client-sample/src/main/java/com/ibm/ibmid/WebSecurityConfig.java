package com.ibm.ibmid;

import com.ibm.ibmid.client.config.IbmIdClientWebSecurityConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final @NonNull IbmIdClientWebSecurityConfig ibmIdClientWebSecurityConfig;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .apply(ibmIdClientWebSecurityConfig)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();

    }

}