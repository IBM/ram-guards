package com.ibm.ram.guards.authorizationserver.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface RamGuardsUserDetailsService {
    UserDetails loadUserByRamGuardsUser(RamGuardsUser ramGuardsUser) throws UsernameNotFoundException;
}
