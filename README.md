# RAM-GUARDS

:full_moon_with_face: Resource Access Management --- Grant User Authorities-Roles-Details Service :new_moon_with_face:

based on this: http://seanthefish.com/2020/07/24/micro-service-authorization/index.html

## Overview

RAM-GUARDS is a service help you manage users' resources easier and more flexible in spring boot framework. And it provides a best practice to manager users' resources under spring cloud micro service framework.

In RAM-GUARDS, each user has a three level authority control: authorities-roles-details, resources are under control by authority level and you can select and customize the other level, which gives system designers a lot flexibility. Also you can imeplent your own authorities-roles-details service by DB, webservice or any other way you want. And for ibmers, you can simply use esw-ram provider to manage your authorities-roles-details which RAM-GUARDS default supports.

Developers can also use RAM-GUARDS easily with just a few configurations by two spring boot starter: `ram-guards-authorization-server-spring-boot-starter` and `ram-guards-resource-server-spring-boot-starter`.

*For ibmers:* For ibmers who use ibm-id to provide authentication and esw-ram to provide users' authorities-roles-details service, we also provide a `ibmid-resource-server-spring-boot-starter` and a `ram-guards-zuul-esw-provider-spring-cloud-starter` to help developers configurate easier with spring cloud micro service framework under [ibm-id oidc](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html) protect.

*RAM-GUARDS currently only supports java8+, spring boot 2.1.0+*

## Features

- Three level authority, you can select any levels you want
- Customize details level, give you more flexibility
- Customize service to get your own users' resources(with restful api, db, etc.)
- JWE encryption through communication
- Redis cache to speed up request
- A `ram-guards-authorization-server-spring-boot-starter` implements OAuth2 password and refresh token grant type
- A `ram-guards-resource-server-spring-boot-starter` implements spring security to protect your endpoints and users' resources 
- *For ibmers:* A `ibmid-resource-server-spring-boot-starter` to provide authentication add protect your spring boot application under [ibm-id oidc](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html) in OAuth2 resource server way.
- *For ibmers:* A `ibmid-client-spring-boot-starter` to provide authentication add protect your spring boot application under [ibm-id oidc](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html) in OAuth2 client way.
- *For ibmers:* A `ram-guards-zuul-esw-provider-spring-cloud-starter` to help developers configurate easier with spring cloud micro service framework using esw-ram authorities-roles-details service provider.
- *For ibmers:* Provide ibm system partner usage with a simple and secure way.(different from ibm users usage)

## How to use

- For authorization server:
  - How to configurate and use your RAM-GUARDS authorization server with `ram-guards-authorization-server-spring-boot-starter` :point_right: [:waxing_crescent_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-authorization-server-spring-boot-starter)
- For resource server:
  - How to configurate and use your RAM-GUARDS resource server with `ram-guards-resource-server-spring-boot-starter` :point_right: [:first_quarter_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-resource-server-spring-boot-starter)
- For spring cloud:
  - What is the best practice to manage users' resources with spring cloud micro service framework using RAM-GUARDS :point_right: [:waxing_gibbous_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/blob/master/spring-cloud-best-practice.md)
- *For using ibm-id authentication:*
  - How to configurate and use your spring boot application under [ibm-id oidc](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html) protection in OAuth2 resource server way with `ibmid-resource-server-spring-boot-starter` :point_right: [:full_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ibmid-resource-server-spring-boot-starter)
  - How to configurate and use your spring boot application under [ibm-id oidc](https://ies-provisioner.prod.identity-services.intranet.ibm.com/tools/sso/home.html) protection in OAuth2 client way with `ibmid-client-spring-boot-starter` :point_right: [:waning_gibbous_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ibmid-client-spring-boot-starter)
- *For using esw-ram provider:*
  - How to configurate and use your spring cloud zuul using esw-ram authorities-roles-details service provider with `RAM-GUARDS zuul esw provider spring cloud starter` :point_right: [:last_quarter_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/tree/master/ram-guards-zuul-esw-provider-spring-cloud-starter#how-to-configurate)
- *For using system partner call:*
  - How to use system partner call in RAM-GUARDS :point_right: [:waning_crescent_moon:](https://github.ibm.com/Danube-Engine/RAM-GUARDS/blob/master/guide_to_use_system_call.md)
- *A PPT of RAM-GUARDS sharing:*
  - RAM-GUARDS sharing :point_right: [:new_moon:](https://docs.google.com/presentation/d/1fj-eOrht2Lw5TgFebZM1loleGWpHgELp2wJSEEBs2e4/edit?usp=sharing)
  
## How to run sample

- First read the documentation above to do some requisite configuration

- Run eureka server
  - run `./gradlew bootJar -p ram-guards-sample-eureka-server`
  - run `java -jar ram-guards-sample-eureka-server/build/libs/ram-guards-sample-eureka-server-0.0.1-SNAPSHOT-boot.jar`
- Run RAM-GUARDS authorization server
  - run `./gradlew bootJar -p ram-guards-sample-authorization-server`
  - run `java -jar ram-guards-sample-authorization-server/build/libs/ram-guards-sample-authorization-server-0.0.1-SNAPSHOT-boot.jar`
- Run RAM-GUARDS resource server
  - run `./gradlew bootJar -p ram-guards-sample-resource-server`
  - run `java -jar ram-guards-sample-resource-server/build/libs/ram-guards-sample-resource-server-0.0.1-SNAPSHOT-boot.jar`
- Run RAM-GUARDS esw-ram provider zuul
  - run `./gradlew bootJar -p ram-guards-sample-zuul`
  - run `java -jar ram-guards-sample-zuul/build/libs/ram-guards-sample-zuul-0.0.1-SNAPSHOT-boot.jar`

## How to test sample

- Send request to get RAM-GUARDS access_token and refresh_token:

```http
POST /authorize/oauth/token HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_id_token_here}

Params: grant_type=password
```

- Send request to refresh RAM-GUARDS access_token and refresh_token:

```http
POST /authorize/oauth/token HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_id_token_here}

Params: grant_type=refresh_token
&refresh_token={RAM_GUARDS_refresh_token_here}
```

- Send request to access RAM-GUARDS resource server and get authorities-roles-details:

```http
GET /client/hi HTTP/1.1
Host: localhost:8769
Header: Authorization: Bearer {ibm_id_id_token_here}
        Ram-Guards: {RAM_GUARDS_access_token_here}
```

## see the ppt inside to get more info
