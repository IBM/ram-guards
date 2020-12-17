package com.ibm.ram.guards.resourceserver.oauth2.validator;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;


import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_ID;

/**
 * @author seanyu
 */
public class RamGuardsJweClientIdValidator implements OAuth2TokenValidator<Jwt> {

    private static OAuth2Error INVALID_CLIENT_ID =
            new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    "This client_id claim is not equal to the configured client id",
                    "https://tools.ietf.org/html/rfc6750#section-3.1");

    private final String clientId;

    public RamGuardsJweClientIdValidator(String clientId) {
        Assert.notNull(clientId, "clientId cannot be null");
        this.clientId = clientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        Assert.notNull(token, "token cannot be null");

        if (this.clientId.equals(token.getClaims().get(CLIENT_ID))) {
            return OAuth2TokenValidatorResult.success();
        } else {
            return OAuth2TokenValidatorResult.failure(INVALID_CLIENT_ID);
        }
    }
}
