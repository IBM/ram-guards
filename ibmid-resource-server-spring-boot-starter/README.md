# IBM id resource server spring boot starter

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
>>>>>>> 30c2c67... init commit
=======
}

dependencies {
    compile('com.ibm.ram.guards:ibmid-resource-server-spring-boot-starter:0.0.1-beta')
>>>>>>> 73cc997... modify readme
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
        <artifactId>ibmid-resource-server-spring-boot-starter</artifactId>
        <version>2.0.12-beta</version>
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
ibm-id:
  resource-server:
    jwk-set-endpoint: https://prepiam.ice.ibmcloud.com/oidc/endpoint/default/jwks
    client-id: {ibm_id_client_id}
    client-secret: {ibm_id_client_secret}
    token-introspection-endpoint: https://prepiam.ice.ibmcloud.com/oidc/endpoint/default/introspect
    system-partners:
      - {system-partner-ibmid-client-id-1}
      - {system-partner-ibmid-client-id-2}
```

### Add spring security configuration

```java
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final @NonNull IbmIdResourceServerWebSecurityConfig ibmIdResourceServerWebSecurityConfig;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .apply(ibmIdResourceServerWebSecurityConfig)
                .and()
                // apply if you also want to use system partner basic call
                // .apply(systemPartnerBasicWebSecurityConfig)
                // .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    // you can exclude the endpoint you don't want to protect by ibm-id oidc here
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**");
    }

}
```

## How to use

After the configuration, your application will automaticly be protected by ibm-id oidc, when you request your endpoint, you must add a ibm-id id-token as authorization bearer token in request header. The kay-value should be like this: `Authorization: Bearer {your id token here}`.

If you want to get the request user's info, you should pass an additional parameter: `@AuthenticationPrincipal Jwt jwt`, it should be like this:

```java
@GetMapping("/getRequestUsername")
    public String getRequestUsername(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getSubject();
    }
```

Also we implemented for IBM system partner call, for IBM system partner, when you request your endpoint, you must add a ibm-id client_credentials access_token as authorization bearer token in request header. `ibmid-resource-server-spring-boot-starter` will provide an anonymous user with some basic info("sub", "uniqueSecurityName", "email"), if you need more info, you should add it by `ibmIdJwtDecoder.setSystemPartnerAdditionalClaims()`. See more details for [A guide to use system partner mode in Ram-Guards
](https://github.ibm.com/Danube-Engine/RAM-GUARDS/blob/master/guide_to_use_system_call.md)

For more information of the details of ibm-id id-token structure, please check [ibm-id documentation](https://w3-connections.ibm.com/wikis/home?lang=en-us#!/wiki/38e9d23f-e3d9-4ddd-83a6-1e894ca99766/page/Adoption%20Application%20%26%20Quick%20Start%20Guide).
