# RAM-GUARDS zuul esw provider spring cloud starter

## Overview

If you use esw provider and ibm-id authorization in spring cloud framework, you can use this spring cloud starter to help you easily implement the best practice of managing users' resources under spring cloud micro service framework using RAM-GUARDS.

You should always remember that this starter must be used with [ibmid-resource-server-spring-boot-starter](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ibmid-resource-server-spring-boot-starter) and [spring-cloud-starter-netflix-zuul](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-zuul) together.

## How to configurate

### Add dependency using Gradle

```gradle
repositories {
    mavenCentral()
    maven { url "https://repo.spring.io/milestone" }
<<<<<<< HEAD
<<<<<<< HEAD
}

dependencies {
    compile('com.ibm.ram.guards:ibmid-resource-server-spring-boot-starter:0.0.1-beta')
    compile('com.ibm.ram.guards:ram-guards-zuul-esw-provider-spring-cloud-starter:0.0.1-beta')
=======
    maven {
        url "https://na.artifactory.swg-devops.com/artifactory/eswchina-generic-local"
        credentials {
            username = "${artifactory_user}"
            password = "${artifactory_api_key}"
        }
    }
}

dependencies {
    compile('com.ibm.ram.guards:ibmid-resource-server-spring-boot-starter:2.0.12-beta')
    compile('com.ibm.ram.guards:ram-guards-zuul-esw-provider-spring-cloud-starter:2.0.12-beta')
>>>>>>> 30c2c67... init commit
=======
}

dependencies {
    compile('com.ibm.ram.guards:ibmid-resource-server-spring-boot-starter:0.0.1-beta')
    compile('com.ibm.ram.guards:ram-guards-zuul-esw-provider-spring-cloud-starter:0.0.1-beta')
>>>>>>> 73cc997... modify readme
    implementation('org.springframework.cloud:spring-cloud-starter-netflix-zuul')
    implementation('org.springframework.cloud:spring-cloud-starter-netflix-eureka-server')
    compileOnly('org.projectlombok:lombok')
}
```


<<<<<<< HEAD
<<<<<<< HEAD
=======
### Add dependency using Maven

First add `settings.xml` in your local maven user’s install: `${user.home}/.m2/settings.xml`

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>eswchina-generic-local</id>
            <username>${artifactory_user}</username>
            <password>${artifactory_api_key}</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <repositories>
                <repository>
                    <id>eswchina-generic-local</id>
                    <name>eswchina generic local</name>
                    <url>https://na.artifactory.swg-devops.com/artifactory/eswchina-generic-local</url>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
</settings>
```

Then modify your `pom.xml`

```xml
</dependencies>
    <dependency>
        <groupId>com.ibm.ram.guards</groupId>
        <artifactId>ram-guards-zuul-esw-provider-spring-cloud-starter</artifactId>
        <version>2.0.12-beta</version>
    </dependency>
    <dependency>
        <groupId>com.ibm.ram.guards</groupId>
        <artifactId>ibmid-resource-server-spring-boot-starter</artifactId>
        <version>2.0.12-beta</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
<repositories>
    <repository>
        <id>eswchina-generic-local</id>
        <name>eswchina generic local</name>
        <url>https://na.artifactory.swg-devops.com/artifactory/eswchina-generic-local</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

>>>>>>> 30c2c67... init commit
=======
>>>>>>> 73cc997... modify readme
### Add configuration in application.yml

```yml
ram-guards:
  authorization-server:
    client-id: {your_client_id_here}
    client-secret: {your_client_secret_here}
    default-password: {your_default_password_here}
    system-partners:
      - {system-partner-ibmid-client-id-1}
      - {system-partner-ibmid-client-id-2}
  zuul:
    # your authorization-server zuul service-id, should be same with your configuration in zuul route
    authorization-server-service-id: service-authorize
zuul:
  routes:
    authorize:
      # your authorization-server zuul route
      path: /authorize/**
      # your authorization-server id
      serviceId: service-authorize
      # 'sensitiveHeaders' must set, to pass the requisite headers
      sensitiveHeaders: Cookie,Set-Cookie
    client:
      # other service zuul route
      path: /client/**
      # other service id
      serviceId: service-hi
      sensitiveHeaders: Cookie,Set-Cookie
# set a timeout in case authorization-server return slowly
ribbon:
  ReadTimeout: 120000
  ConnectTimeout: 12000
```

### Add spring boot configuration

```java
// enable spring cloud zuul, you also need a eureka server
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class EurekaClientZuul {

    public static void main(String[] args) {
        SpringApplication.run( EurekaClientZuul.class, args );
    }
}
```

## How to use

After the configuration, your spring cloud application will automaticly be protected by [ibm-id oidc](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html) and you can get the request ibm-id user's access_token and refresh_token from the spring cloud zuul.

You can get your RAM-GUARDS access_token and refresh_token with this request:

```http
POST /authorize/oauth/token HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_id_token_here}

Params: grant_type=password
```

You can refresh your RAM-GUARDS access_token and refresh_token with this request:

```http
POST /authorize/oauth/token HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_id_token_here}

Params: grant_type=refresh_token
&refresh_token={RAM_GUARDS_refresh_token_here}
```

You can access your other eureka service endpoint(other eureka service should config with [ram-guards-resource-server-spring-boot-starter](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-resource-server-spring-boot-starter)) with this request:

```http
GET /client/{some_endpoint} HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_id_token_here}
        Ram-Guards: {RAM_GUARDS_access_token_here}
```

The access_token and refresh_token are in [JWE](https://tools.ietf.org/html/rfc7516) format, currently we are using JWE with a shared symmetric key, which is your client secret, so it's very important to keep your client secret safe and use https while sending request.
