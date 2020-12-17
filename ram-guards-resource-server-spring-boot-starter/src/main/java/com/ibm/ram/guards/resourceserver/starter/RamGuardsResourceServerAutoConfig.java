package com.ibm.ram.guards.resourceserver.starter;

import com.ibm.ram.guards.resourceserver.oauth2.converter.RamGuardsJweAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author seanyu
 */
@ConditionalOnClass({RamGuardsJweAuthenticationConverter.class})
@EnableConfigurationProperties({RamGuardsResourceServerProperties.class})
@Configuration
@ComponentScan("com.ibm.ram.guards.resourceserver")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsResourceServerAutoConfig {

    @Bean
    @ConditionalOnMissingBean(RamGuardsJweAuthenticationConverter.class)
    public RamGuardsJweAuthenticationConverter ramJwtAuthenticationConverter() {
        return new RamGuardsJweAuthenticationConverter();
    }

}
