package com.ibm.ram.guards.authorizationserver.userdetails;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;


import static com.ibm.ram.guards.authorizationserver.starter.RamGuardsRedisCacheAutoConfig.AUTHORIZATION_RAM_SERVER_CACHE_NAME;


/**
 * @author seanyu
 */
@Component
@Slf4j
@Qualifier("ramGuardsUserDetailsManager")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsUserDetailsManager implements RamGuardsUserDetailsService {

    private final @NonNull RamGuardsUserDetailsServiceProvider ramGuardsUserDetailsServiceProvider;

    @Override
    @Cacheable(cacheNames = AUTHORIZATION_RAM_SERVER_CACHE_NAME,
            key = "#p0.redisKey",
            cacheManager = "redisCacheManager",
            unless="#result == null or #result.authorities == null or #result.authorities.size() == 0 or (#result.authorities.size() == 1 and #result.authorities[0].authority == 'anonymous')")
    public UserDetails loadUserByRamGuardsUser(RamGuardsUser ramGuardsUser) throws UsernameNotFoundException {
        log.info("Load user: {}'s UserDetails without caching", ramGuardsUser.getName());
        try {
            return ramGuardsUserDetailsServiceProvider.loadUserByRamGuardsUser(ramGuardsUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
