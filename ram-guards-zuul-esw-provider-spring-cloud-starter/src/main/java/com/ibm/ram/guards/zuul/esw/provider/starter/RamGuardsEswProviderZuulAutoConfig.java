package com.ibm.ram.guards.zuul.esw.provider.starter;

import com.ibm.ram.guards.zuul.esw.provider.filter.RamGuardsEswProviderZuulFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.RoutesEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.Filter;

/**
 * @author seanyu
 */
@ConditionalOnClass({RamGuardsEswProviderZuulFilter.class})
@EnableConfigurationProperties({RamGuardsEswProviderZuulProperties.class, RamGuardsAuthorizationServerProperties.class})
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ComponentScan("com.ibm.ram.guards.zuul.esw.provider")
public class RamGuardsEswProviderZuulAutoConfig {

    private final @NonNull RamGuardsEswProviderZuulProperties ramGuardsEswProviderZuulProperties;

    private final @NonNull RamGuardsAuthorizationServerProperties ramGuardsAuthorizationServerProperties;

    private final @NonNull RoutesEndpoint routesEndpoint;

    private final @NonNull HandlerMappingIntrospector handlerMappingIntrospector;

    /**
     * bean registered by {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration} springSecurityFilterChain();
     */
    private final @NonNull @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain;

    @Bean
    @ConditionalOnMissingBean(RamGuardsEswProviderZuulFilter.class)
    public RamGuardsEswProviderZuulFilter ramGuardsEswProviderZuulFilter() throws Exception {
        RamGuardsEswProviderZuulFilter ramGuardsEswProviderZuulFilter = new RamGuardsEswProviderZuulFilter();
        ramGuardsEswProviderZuulFilter.setRamGuardsAuthorizationServerProperties(ramGuardsAuthorizationServerProperties);
        ramGuardsEswProviderZuulFilter.setRamGuardsEswProviderZuulProperties(ramGuardsEswProviderZuulProperties);
        ramGuardsEswProviderZuulFilter.setRoutesEndpoint(routesEndpoint);
        ramGuardsEswProviderZuulFilter.setHandlerMappingIntrospector(handlerMappingIntrospector);
        ramGuardsEswProviderZuulFilter.setFilterChainProxy((FilterChainProxy) springSecurityFilterChain);
        return ramGuardsEswProviderZuulFilter;
    }
}
