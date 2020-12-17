package com.ibm.ibmid.resourceserver.oauth2.decoder;

import com.ibm.ibmid.resourceserver.starter.IbmIdResourceServerProperties;
import com.ibm.ram.guards.helper.RSAHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.security.oauth2.common.util.OAuth2Utils.CLIENT_ID;
import static org.springframework.security.oauth2.common.util.OAuth2Utils.GRANT_TYPE;

/**
 * @author seanyu
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IbmIdJwtDecoder implements JwtDecoder {

    private final @NonNull IbmIdResourceServerProperties ibmIdResourceServerProperties;

    private final @NonNull RemoteTokenServices remoteTokenServices;

    private final OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();

    private Map<String, Object> systemPartnerAdditionalClaims;

    @Override
    public Jwt decode(String token) throws JwtException {
        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = null;
        try {
            if (!StringUtils.isEmpty(ibmIdResourceServerProperties.getJwkSetEndpoint())){
                nimbusReactiveJwtDecoder = new NimbusReactiveJwtDecoder(ibmIdResourceServerProperties.getJwkSetEndpoint());
            }else if (!StringUtils.isEmpty(ibmIdResourceServerProperties.getJwkCertificatePath())){
                nimbusReactiveJwtDecoder = new NimbusReactiveJwtDecoder(RSAHelper.readRsaPublicKey(ibmIdResourceServerProperties.getJwkCertificatePath()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (token.split("\\.").length != 3){
            // not a jwt
            this.authenticationManager.setTokenServices(remoteTokenServices);
            BearerTokenAuthenticationToken authenticationRequest = new BearerTokenAuthenticationToken(token);
            try {
                OAuth2Authentication authenticationResult = (OAuth2Authentication) this.authenticationManager.authenticate(authenticationRequest);
                String grantType = authenticationResult.getOAuth2Request().getRequestParameters().get(GRANT_TYPE);
                Map<String, Object> claimsMap = new HashMap<>();
                // in ibm id client_credentials grant type, sub is the same as client id, other grant types is username
                String subject = authenticationResult.getOAuth2Request().getRequestParameters().get("sub");
                String username = authenticationResult.getOAuth2Request().getRequestParameters().get("preferred_username");
                if ("client_credentials".equals(grantType)){
                    // client_credentials system partner mode
                    log.info("request to client_credentials system partner mode");
                    if (CollectionUtils.isEmpty(ibmIdResourceServerProperties.getSystemPartners())) {
                        throw new JwtException("ibm id resources server don't have any system partner");
                    }
                    if (!ibmIdResourceServerProperties.getSystemPartners().contains(authenticationResult.getOAuth2Request().getRequestParameters().get(CLIENT_ID))){
                        throw new JwtException("invalid System-Partner");
                    }else {
                        if (systemPartnerAdditionalClaims == null) {
                            this.systemPartnerAdditionalClaims = new HashMap<>();
                        }
                        systemPartnerAdditionalClaims.put("system_partner", true);
                        Map<String, Object> defaultClaimsMap  = new HashMap<>();
                        defaultClaimsMap.put("scope", "openid");
                        defaultClaimsMap.put("grant_type", "client_credentials");
                        defaultClaimsMap.put("sub", subject);
                        claimsMap.putAll(defaultClaimsMap);
                        claimsMap.putAll(systemPartnerAdditionalClaims);
                        log.info("system partner: {}'s ibm-id client-credentials access-token validated success with System-Partner mode", subject);
                    }
                }else {
                    if (!CollectionUtils.isEmpty(ibmIdResourceServerProperties.getSystemPartners()) && ibmIdResourceServerProperties.getSystemPartners().contains(username)){
                        log.info("request to user system partner mode");
                        if (systemPartnerAdditionalClaims == null) {
                            this.systemPartnerAdditionalClaims = new HashMap<>();
                        }
                        systemPartnerAdditionalClaims.put("system_partner", true);
                        claimsMap.putAll(authenticationResult.getOAuth2Request().getRequestParameters());
                        claimsMap.putAll(systemPartnerAdditionalClaims);
                        log.info("system partner: {}'s ibm-id access-token validated success with System-Partner mode", username);
                    }else {
                        log.info("request to access token mode");
                        claimsMap.putAll(authenticationResult.getOAuth2Request().getRequestParameters());
                        log.info("user: {}'s ibm-id access-token validated success with access-token mode", username);
                    }
                }
                return new Jwt(token,
                        Instant.ofEpochSecond(Long.parseLong(authenticationResult.getOAuth2Request().getRequestParameters().get("iat"))),
                        Instant.ofEpochSecond(Long.parseLong(authenticationResult.getOAuth2Request().getRequestParameters().get("exp"))),
                        Collections.singletonMap("alg", "HS256"),
                        claimsMap);
            } catch (AuthenticationException failed) {
                log.error("access token validate failed");
                failed.printStackTrace();
                throw new JwtException("access token validate failed", failed);
            } catch (Exception e) {
                log.error("unexpect exception happened while access token validation");
                e.printStackTrace();
                throw new JwtException("unexpect exception happened while access token validation", e);
            }
        } else {
            Jwt jwt = Objects.requireNonNull(nimbusReactiveJwtDecoder).decode(token).block();
            String username = (String) Objects.requireNonNull(jwt).getClaims().get("preferred_username");
            if (!CollectionUtils.isEmpty(ibmIdResourceServerProperties.getSystemPartners()) && ibmIdResourceServerProperties.getSystemPartners().contains(username)){
                log.info("request to user system partner mode");
                Map<String, Object> newSystemPartnerJwtClaims = new HashMap<>(jwt.getClaims());
                if (systemPartnerAdditionalClaims == null) {
                    this.systemPartnerAdditionalClaims = new HashMap<>();
                }
                systemPartnerAdditionalClaims.put("system_partner", true);
                newSystemPartnerJwtClaims.putAll(systemPartnerAdditionalClaims);
                log.info("system partner: {}'s ibm-id id-token validated success with System-Partner mode", username);
                return new Jwt(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), newSystemPartnerJwtClaims);
            }else {
                log.info("request to id token mode");
                log.info("user: {}'s ibm-id id-token validated success with id-token mode", username);
                return jwt;
            }
        }
    }

    public void setSystemPartnerAdditionalClaims(Map<String, Object> systemPartnerAdditionalClaims) {
        this.systemPartnerAdditionalClaims = systemPartnerAdditionalClaims;
    }

}
