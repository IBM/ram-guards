# IBM id client spring boot starter

## How to configurate

### Add dependency using Gradle

```gradle
repositories {
    mavenCentral()
    maven { url "https://repo.spring.io/milestone" }
}

dependencies {
    compile('com.ibm.ram.guards:ibmid-client-spring-boot-starter:0.0.1-beta')
    compileOnly('org.projectlombok:lombok')
}
```

### Add configuration in application.yml

```yml
ibm-id:
  client:
    client-id: {ibm_id_client_id}
    client-secret: {ibm_id_client_secret}
    authorization-uri: https://prepiam.ice.ibmcloud.com/oidc/endpoint/default/authorize
    token-uri: https://prepiam.ice.ibmcloud.com/oidc/endpoint/default/token
    redirect-uri: https://localhost:9876/login
    jwk-set-uri: https://prepiam.ice.ibmcloud.com/oidc/endpoint/default/jwks
```

### Add spring security configuration

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final @NonNull IbmIdClientWebSecurityConfig ibmIdClientWebSecurityConfig;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .apply(ibmIdClientWebSecurityConfig)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();

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

After the configuration, your application will automaticly be protected by ibm-id oidc.

If you want to get the request user's info, you should pass an additional parameter: `Authentication authentication`, then call `getCredentials()` and cast to `IbmIdOidcToken`, it should be like this:

```java
    @GetMapping("/token")
    @ResponseBody
    public Map<String, String> getToken(Authentication authentication){
        IbmIdOidcToken ibmIdOidcToken = (IbmIdOidcToken) authentication.getCredentials();
        Map<String, String> map = new HashMap<>();
        map.put("accesstoken", ibmIdOidcToken.getValue());
        map.put("refreshtoken", ibmIdOidcToken.getRefreshToken().getValue());
        map.put("idtoken", ibmIdOidcToken.getIdToken().getTokenValue());
        return map;
    }
```

For more information of the details of ibm-id id-token structure, please check [ibm-id documentation](https://w3-connections.ibm.com/wikis/home?lang=en-us#!/wiki/38e9d23f-e3d9-4ddd-83a6-1e894ca99766/page/Adoption%20Application%20%26%20Quick%20Start%20Guide).
