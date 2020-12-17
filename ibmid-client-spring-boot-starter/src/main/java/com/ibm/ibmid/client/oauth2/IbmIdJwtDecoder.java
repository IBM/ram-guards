package com.ibm.ibmid.client.oauth2;

import com.ibm.ibmid.client.starter.IbmIdClientAutoConfig;
import com.ibm.ibmid.client.starter.IbmIdClientProperties;
import com.ibm.ram.guards.helper.RSAHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author seanyu
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IbmIdJwtDecoder {

    private final @NonNull IbmIdClientProperties ibmIdClientProperties;

    public Jwt decode(String token) throws JwtException {
        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = null;
        try {
            if (!StringUtils.isEmpty(ibmIdClientProperties.getJwkSetUri())){
                nimbusReactiveJwtDecoder = new NimbusReactiveJwtDecoder(ibmIdClientProperties.getJwkSetUri());
            }else if (!StringUtils.isEmpty(ibmIdClientProperties.getJwkCertificatePath())){
                nimbusReactiveJwtDecoder = new NimbusReactiveJwtDecoder(RSAHelper.readRsaPublicKey(ibmIdClientProperties.getJwkCertificatePath()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Jwt jwt = Objects.requireNonNull(nimbusReactiveJwtDecoder).decode(token).block();
        log.info("user: {}'s ibm-id id-token validated success", Objects.requireNonNull(jwt).getClaims().get("preferred_username"));
        return jwt;
    }


}
