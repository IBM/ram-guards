package com.ibm.ram.guards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author seanyu
 */
@SpringBootApplication
@EnableDiscoveryClient
public class EurekaClientResourceServer {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientResourceServer.class, args);
    }

}
