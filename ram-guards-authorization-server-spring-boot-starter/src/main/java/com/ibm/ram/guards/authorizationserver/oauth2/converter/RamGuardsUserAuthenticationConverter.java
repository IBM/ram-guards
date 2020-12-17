package com.ibm.ram.guards.authorizationserver.oauth2.converter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Collection;
import java.util.Map;


public class RamGuardsUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        return super.convertUserAuthentication(authentication);
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) super.extractAuthentication(map);
        usernamePasswordAuthenticationToken.setDetails(map);
        return usernamePasswordAuthenticationToken;
    }

}
