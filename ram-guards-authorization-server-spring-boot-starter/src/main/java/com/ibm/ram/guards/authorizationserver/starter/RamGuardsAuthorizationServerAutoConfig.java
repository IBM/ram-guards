package com.ibm.ram.guards.authorizationserver.starter;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author seanyu
 */
@EnableConfigurationProperties({RamGuardsAuthorizationServerProperties.class})
@Configuration
@ComponentScan(basePackages="com.ibm.ram.guards.authorizationserver")
public class RamGuardsAuthorizationServerAutoConfig {
}
