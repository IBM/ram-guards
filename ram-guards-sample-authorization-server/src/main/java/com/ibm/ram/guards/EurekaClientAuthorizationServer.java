package com.ibm.ram.guards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author seanyu
 */
@SpringBootApplication
public class EurekaClientAuthorizationServer {
  public static void main(String[] args) {
      SpringApplication.run(EurekaClientAuthorizationServer.class,args);
  }
}
