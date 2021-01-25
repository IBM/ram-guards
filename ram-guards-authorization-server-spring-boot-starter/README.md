# RAM-GUARDS authorization server spring boot starter

## How to configurate

### Add dependency using Gradle

```gradle
repositories {
    mavenCentral()
    maven { url "https://repo.spring.io/milestone" }
}

dependencies {
    compile('com.ibm.ram.guards:ram-guards-authorization-server-spring-boot-starter:0.0.1-beta')
}
```


### Add configuration in application.yml

if you don't need cache your user' resource info in redis:

```yml
ram-guards:
  authorization-server:
    client-id: {your_client_id_here}
    # client-secret must be a AES secret key, you can run `AESHelper.generateSecretKeyString()` to get a valid client-secret
    client-secret: {your_client_secret_here}
    default-password: {your_default_password_here}
    token-ttl: 3000
    enable-redis-cache: false
    system-partners:
      - {system-partner-ibmid-client-id-1}
      - {system-partner-ibmid-client-id-2}
```

if you need cache:

```yml
spring:
  redis:
    host: localhost
    port: 6379
ram-guards:
  authorization-server:
    client-id: {your_client_id_here}
    # client-secret must be a AES secret key, you can run `AESHelper.generateSecretKeyString()` to get a valid client-secret
    client-secret: {your_client_secret_here}
    default-password: {your_default_password_here}
    token-ttl: 3000
    enable-redis-cache: true
    system-partners:
      - {system-partner-ibmid-client-id-1}
      - {system-partner-ibmid-client-id-2}
```

if you are using esw-ram provide user authorities-roles-details service:

```yml
spring:
  redis:
    host: localhost
    port: 6379
ram-guards:
  authorization-server:
    client-id: {your_client_id_here}
    # client-secret must be a AES secret key, you can run `AESHelper.generateSecretKeyString()` to get a valid client-secret
    client-secret: {your_client_secret_here}
    default-password: {your_default_password_here}
    token-ttl: 3000
    enable-redis-cache: true
    system-partners:
      - {system-partner-ibmid-client-id-1}
      - {system-partner-ibmid-client-id-2}
    esw-provider:
      enabled: true
      url: {esw_ram_url}
      username: {esw_ram_username}
      password: {esw_ram_password}
      application-id: {esw_ram_application_id}
      system-partner-function-id:
        {system-partner-ibmid-client-id-1}: {system-partner-function-id-1}
        {system-partner-ibmid-client-id-2}: {system-partner-function-id-2}
```

if you are using your own authorities-roles-details service, you also need to implements interface `RamGuardsAuthorityService` and override method `List<RamGuardsAuthority> getUserAuthorities(String username)` to provide your own implemention.

## How to use

After the configuration, your application will automaticly became a OAuth2 server that implement password ang refresh_token grant type.

You can get your RAM-GUARDS access_token and refresh_token with this request:

```http
POST /oauth/token HTTP/1.1
Host: localhost:8766
Headers: Authorization: Basic {'your_client_id:your_client_secret' in base64 format}

Params: scope=authority role detail
&grant_type=password
&username={request_username_here}
&password={your_default_password_here}
```

The scope parameter can be: `authority role detail`, `authority role`, `authority`, depands on your provider service.

You can refresh your RAM-GUARDS access_token and refresh_token with this request:

```http
POST /oauth/token HTTP/1.1
Host: localhost:8766
Headers: Authorization: Basic {'your_client_id:your_client_secret' in base64 format}

Params: grant_type=refresh_token
&refresh_token={RAM_GUARDS_refresh_token_here}
```

**The access_token and refresh_token are in [JWE](https://tools.ietf.org/html/rfc7516) format, currently we are using JWE with a shared symmetric key(AES), which is your client secret, so it's very important to keep your client secret safe and use https while sending request. Also your client secret must be an AES key.**

## About caching

If you enable cache, RAM-GURADS will cache user's authorities-roles-details in cache with a ttl same as the expire time you set of your access_token.

Every request of password grant will first check if there's data in cache, if has, it will return the data in cache directly, if not, it will call your authorities-roles-details service, store the data in cache then return.

Every request of refresh_token grant will reset the cache, this means every request of refresh_token grant will call your authorities-roles-details service no matter whether the cache has expired, and store the new result data in cache.

Currently, RAM-GUARDS only support cache with redis, you need to provide a bean of `org.springframework.data.redis.connection.RedisConnectionFactory` when you enable cache.

## About client_secret

You can generate your own client_secret by calling `AESHelper.main()`.
