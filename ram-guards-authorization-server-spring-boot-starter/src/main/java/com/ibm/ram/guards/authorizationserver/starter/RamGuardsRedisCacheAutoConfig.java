package com.ibm.ram.guards.authorizationserver.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUserDetails;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Collections;

/**
 * @author seanyu
 */
@ConditionalOnClass(RedisCacheManager.class)
@EnableConfigurationProperties({RamGuardsAuthorizationServerProperties.class})
@ConditionalOnProperty(prefix = "ram-guards.authorization-server",value = "enable-redis-cache", havingValue = "true")
@EnableCaching
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsRedisCacheAutoConfig {

    public static final String AUTHORIZATION_RAM_SERVER_CACHE_NAME = "ram-guards";

    private final @NonNull RamGuardsAuthorizationServerProperties ramGuardsAuthorizationServerProperties;

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("scopeFilter", SimpleBeanPropertyFilter.serializeAll()).setFailOnUnknownId(false);
        objectMapper.setFilterProvider(filterProvider);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(RamGuardsUserDetails.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .prefixKeysWith(AUTHORIZATION_RAM_SERVER_CACHE_NAME + ":" + ramGuardsAuthorizationServerProperties.getClientId() + ":")
                .entryTtl(Duration.ofSeconds(ramGuardsAuthorizationServerProperties.getTokenTtl()))
                .disableCachingNullValues();
        // order matters
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .initialCacheNames(Collections.singleton(AUTHORIZATION_RAM_SERVER_CACHE_NAME))
                .build();
    }


}
