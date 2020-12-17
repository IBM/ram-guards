package com.ibm.ram.guards.authorizationserver.config;

import com.ibm.ram.guards.authorizationserver.oauth2.provider.RamGuardsUserDetailsAuthenticationProvider;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUserDetailsManager;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUserDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author seanyu
 */
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsAuthorizationServerWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final @NonNull RamGuardsUserDetailsManager ramGuardsUserDetailsManager;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        RamGuardsUserDetailsAuthenticationProvider authenticationProvider = new RamGuardsUserDetailsAuthenticationProvider();
        authenticationProvider.setUserDetailsService(ramGuardsUserDetailsManager);
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
    }

}
