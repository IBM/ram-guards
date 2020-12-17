package com.ibm.ram.guards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author seanyu
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class EurekaClientZuul {

    public static void main(String[] args) {
        SpringApplication.run( EurekaClientZuul.class, args );
    }
}
