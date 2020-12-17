# RAM-GUARDS resource server spring boot starter

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
    compile('com.ibm.ram.guards:ram-guards-resource-server-spring-boot-starter:0.0.1-beta')
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
    compile('com.ibm.ram.guards:ram-guards-resource-server-spring-boot-starter:2.0.12-beta')
>>>>>>> 30c2c67... init commit
=======
}

dependencies {
    compile('com.ibm.ram.guards:ram-guards-resource-server-spring-boot-starter:0.0.1-beta')
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
        <artifactId>ram-guards-resource-server-spring-boot-starter</artifactId>
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

if you don't need cache your user' resource info in redis:

```yml
ram-guards:
  resource-server:
    client-id: {your_client_id_here}
    client-secret: {your_client_secret_here}
    system-partners:
      - {system-partner-ibmid-client-id-1}
      - {system-partner-ibmid-client-id-2}
```

### Add spring security configuration

```java
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
        
//        http
//                        .apply(ibmIdResourceServerWebSecurityConfig)
//                        .and()
//                        .authorizeRequests().anyRequest().authenticated();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .mvcMatchers("/service-instances/**");
    }


}
```

## How to use

After the configuration, your application will automaticly be protected by RAM-GUARDS, when you request your endpoint, you must add a RAM-GUARDS access token as authorization bearer token in request header. The kay-value should be like this: `Authorization: Bearer {RAM-GUARDS access token here}`. And the framework will validate your RAM-GUARDS access token, check whether the request user has the authority to request the endpoint and then parse the RAM-GUARDS access token.

If you want to get the request user's resource detail, you should pass an additional parameter: `Authentication authentication`, it should be like this:

```java
    @GetMapping("/getRequestUsernameAndAuthorities")
    public Map<String, Object> getRequestUsernameAndAuthorities(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        Collection<RamGuardsAuthority> ramAuthorities = (Collection<RamGuardsAuthority>) authentication.getAuthorities();
        // return username
        response.put("principal", authentication.getPrincipal());
        // return RAM-GUARDS access token value
        response.put("credentials", authentication.getCredentials());
        // return RAM-GUARDS access token with org.springframework.security.oauth2.jwt.Jwt object
        response.put("details", authentication.getDetails());
        // return user's authority-role-detail
        response.put("authorities", ramAuthorities);
        return response;
    }
```

If you use `ram-guards-zuul-esw-provider-spring-cloud-starter`, you can also get ibm id token claims by pass a parameter: `@RequestHeader("user-info") String userInfo` or `@RequestHeader("id-token") String idToken`, like this:

```java
    @GetMapping("/hi")
    public Map<String, Object> home(Authentication authentication, @RequestHeader("user-info",  required = false) String userInfo, @RequestHeader("id-token") String idToken,  required = false) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Collection<RamGuardsAuthority> ramAuthorities = (Collection<RamGuardsAuthority>) authentication.getAuthorities();
        response.put("principal", authentication.getPrincipal());
        response.put("credentials", authentication.getCredentials());
        response.put("details", authentication.getDetails());
        response.put("authorities", ramAuthorities);
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        Map<String,Object> userInfoMap = new ObjectMapper().readValue(userInfo, typeRef);
        response.put("userInfos", userInfoMap);
        response.put("idToken", idToken);
        return response;
    }
```

If you want to call other service, don't forget to add RAM-GUARDS access token as authorization bearer token in request header(`Authorization: Bearer {RAM-GUARDS access token here}`). If you use `org.springframework.web.client.RestTemplate`, you can create a default header and use like this:

```java
RestTemplate restTemplate = new RestTemplate();
HttpHeaders httpHeaders = RamGuardsRestTemplateHelper.createRamGuardsHeaders(authentication, idTokenClaims);
restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<T>(httpHeaders), clazz);
```

If you want to deserialize RAM-GUARDS entity, please use `RamGuardsJacksonHelper.DeserializeObjectMapper`.
