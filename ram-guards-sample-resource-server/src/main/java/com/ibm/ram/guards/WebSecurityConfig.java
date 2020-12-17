package com.ibm.ram.guards;

import com.ibm.ram.guards.resourceserver.config.RamGuardsResourceServerWebSecurityConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.ibm.ram.guards.resourceserver.constant.RamGuardsAuthorityConstant.ANONYMOUS;
import static com.ibm.ram.guards.resourceserver.constant.RamGuardsAuthorityConstant.SYSTEM_PARTNER;


/**
 * @author seanyu
 */
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final @NonNull RamGuardsResourceServerWebSecurityConfig ramGuardsResourceServerWebSecurityConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .apply(ramGuardsResourceServerWebSecurityConfig)
                .and()
                .authorizeRequests()
                .mvcMatchers("/hi","/check/**").hasAnyAuthority("PRICE", SYSTEM_PARTNER, ANONYMOUS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .mvcMatchers("/service-instances/**");
    }
}
