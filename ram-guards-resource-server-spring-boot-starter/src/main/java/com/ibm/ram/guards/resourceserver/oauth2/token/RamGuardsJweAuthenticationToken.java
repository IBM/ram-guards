package com.ibm.ram.guards.resourceserver.oauth2.token;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

/**
 * @author seanyu
 */
public class RamGuardsJweAuthenticationToken extends JwtAuthenticationToken {
    public RamGuardsJweAuthenticationToken(Jwt jwt) {
        super(jwt);
    }

    public RamGuardsJweAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
    }

    @Override
    public Object getPrincipal() {
        Jwt jwt = super.getToken();
        return jwt.getSubject();
    }

    @Override
    public Object getCredentials() {
        Jwt jwt = super.getToken();
        return jwt.getTokenValue();
    }

    @Override
    public Object getDetails() {
        return super.getToken();
    }

}
