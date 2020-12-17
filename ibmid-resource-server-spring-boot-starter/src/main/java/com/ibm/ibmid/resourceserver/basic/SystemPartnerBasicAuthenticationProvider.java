package com.ibm.ibmid.resourceserver.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class SystemPartnerBasicAuthenticationProvider extends DaoAuthenticationProvider {

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter = new JwtAuthenticationConverter();

    private Map<String, Object> systemPartnerAdditionalClaims;


    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {

        Map<String, Object> claimsMap = new HashMap<>();
        if (systemPartnerAdditionalClaims == null) {
            this.systemPartnerAdditionalClaims = new HashMap<>();
        }
        Map<String, Object> defaultClaimsMap  = new HashMap<>();
        defaultClaimsMap.put("scope", "openid");
        defaultClaimsMap.put("grant_type", "client_credentials");
        // system partner basic username will be "SYSTEM_PARTNER:IBM-SYSTEM"
        defaultClaimsMap.put("sub", "SYSTEM_PARTNER:" + user.getUsername());
        claimsMap.putAll(defaultClaimsMap);
        claimsMap.putAll(systemPartnerAdditionalClaims);
        log.info("system partner basic token validated success with System-Partner mode");

        String basicToken = new String(Base64.getEncoder().encode((authentication.getPrincipal() + ":" + authentication.getCredentials()).getBytes()));

        Jwt jwt = new Jwt(basicToken,
                Instant.now(),
                Instant.ofEpochMilli(Instant.now().toEpochMilli() + 3600000),
                Collections.singletonMap("alg", "HS256"),
                claimsMap);

        AbstractAuthenticationToken token = this.jwtAuthenticationConverter.convert(jwt);
        token.setDetails(authentication.getDetails());

        return token;
    }


}
