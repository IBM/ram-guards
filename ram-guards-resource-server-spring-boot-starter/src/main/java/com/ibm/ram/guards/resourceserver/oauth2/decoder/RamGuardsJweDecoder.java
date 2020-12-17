package com.ibm.ram.guards.resourceserver.oauth2.decoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.helper.JWEHelper;
import com.ibm.ram.guards.resourceserver.oauth2.validator.RamGuardsJweClientIdValidator;
import com.ibm.ram.guards.resourceserver.oauth2.validator.RamGuardsSystemPartnerValidator;
import com.ibm.ram.guards.resourceserver.starter.RamGuardsResourceServerProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsJweDecoder implements JwtDecoder {

    private final @NonNull RamGuardsResourceServerProperties ramGuardsResourceServerProperties;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String EXPIRATION_TIME_CLAIM = "exp";

    private static final String ISSUED_AT_CLAIM = "iat";


    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
            Map<String, Object> headers = objectMapper.readValue(JWEHelper.getJWEHeaderWithCekOrSymmetricKey(token), typeRef);
            Map<String, Object> claims = objectMapper.readValue(JWEHelper.decryptJWEPayloadWithCekOrSymmetricKey(token, ramGuardsResourceServerProperties.getClientSecret()), typeRef);
            Instant expiresAt = null;
            if (claims.get(EXPIRATION_TIME_CLAIM) != null) {
                Integer exp = (Integer) claims.get(EXPIRATION_TIME_CLAIM);
                expiresAt = Instant.ofEpochSecond(exp.longValue());
            }
            Instant issuedAt = null;
            if (claims.get(ISSUED_AT_CLAIM) != null) {
                Integer ias = (Integer) claims.get(ISSUED_AT_CLAIM);
                issuedAt = Instant.ofEpochSecond(ias.longValue());
            } else if (expiresAt != null) {
                // Default to expiresAt - 1 second
                issuedAt = Instant.from(expiresAt).minusSeconds(1);
            }
            Jwt jwt = validateJwt(new Jwt(token, issuedAt, expiresAt, headers, claims));
            log.info("user: {}'s Ram-Guards-Access-Token validated success", jwt.getSubject());
            return jwt;
        } catch (RuntimeException ex) {
            throw new JwtException("An error occurred while attempting to decode the Jwt: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Jwt validateJwt(Jwt jwt) throws JwtValidationException {

        RamGuardsJweClientIdValidator ramGuardsJweClientIdValidator = new RamGuardsJweClientIdValidator(ramGuardsResourceServerProperties.getClientId());
        RamGuardsSystemPartnerValidator ramGuardsSystemPartnerValidator = new RamGuardsSystemPartnerValidator(ramGuardsResourceServerProperties.getSystemPartners());
        JwtTimestampValidator jwtTimestampValidator = new JwtTimestampValidator();
        DelegatingOAuth2TokenValidator delegatingOAuth2TokenValidator =
                new DelegatingOAuth2TokenValidator(Arrays.asList(
                        ramGuardsJweClientIdValidator,
                        jwtTimestampValidator,
                        ramGuardsSystemPartnerValidator
                ));
        OAuth2TokenValidatorResult result = delegatingOAuth2TokenValidator.validate(jwt);

        if ( result.hasErrors() ) {
            String message = result.getErrors().iterator().next().getDescription();
            throw new JwtValidationException(message, result.getErrors());
        }

        return jwt;
    }

}
