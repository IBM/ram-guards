package com.ibm.ram.guards.resourceserver.oauth2.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import com.ibm.ram.guards.resourceserver.oauth2.token.RamGuardsJweAuthenticationToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

/**
 * @author seanyu
 */
public class RamGuardsJweAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) extractAuthorities(jwt);
        return new RamGuardsJweAuthenticationToken(jwt, authorities);
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        return objectMapper.convertValue(jwt.getClaims().get("authorities"), objectMapper.getTypeFactory().constructCollectionType(List.class, RamGuardsAuthority.class));
    }
}

