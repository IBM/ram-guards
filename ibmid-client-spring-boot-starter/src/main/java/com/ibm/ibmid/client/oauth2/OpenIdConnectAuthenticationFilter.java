package com.ibm.ibmid.client.oauth2;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


@Slf4j
@Setter
public class OpenIdConnectAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private OAuth2RestOperations oauth2RestTemplate;

    private IbmIdJwtDecoder ibmIdJwtDecoder;

    public OpenIdConnectAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(new NoOpAuthenticationManager());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

        OAuth2AccessToken accessToken;

        try {
            accessToken = oauth2RestTemplate.getAccessToken();
            log.info("AccessToken: value: " + accessToken.getValue());
            log.info("AccessToken: additionalInfo: " + accessToken.getAdditionalInformation());
            log.info("AccessToken: tokenType: " + accessToken.getTokenType());
            log.info("AccessToken: expiration: " + accessToken.getExpiration());
            log.info("AccessToken: expiresIn: " + accessToken.getExpiresIn());
            log.info("RefreshToken: " + accessToken.getRefreshToken().getValue());

        } catch (OAuth2Exception e) {
            throw new BadCredentialsException("Could not obtain Access Token", e);
        }

        try {
            String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
            Jwt jwt = ibmIdJwtDecoder.decode(idToken);
            IbmIdOidcToken ibmIdOidcToken = new IbmIdOidcToken(accessToken, jwt);
            return new PreAuthenticatedAuthenticationToken(ibmIdOidcToken, ibmIdOidcToken, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        } catch (InvalidTokenException e) {
            throw new BadCredentialsException("Could not obtain user details from Access Token", e);
        }
    }

    private static class NoOpAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication) {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }
    }
}
