package com.ibm.ram.guards.resourceserver.oauth2.validator;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


public class RamGuardsSystemPartnerValidator implements OAuth2TokenValidator<Jwt> {

    private static final String SYSTEM_PARTNER = "SYSTEM_PARTNER";

    private static OAuth2Error INVALID_SYSTEM_PARTNER =
            new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    "invalid system partner",
                    "");

    private static OAuth2Error NO_SYSTEM_PARTNER_PROVIDE =
            new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    "ram guards resources server don't have any system partner",
                    "");

    private List<String> systemPartners;

    public RamGuardsSystemPartnerValidator(List<String> systemPartners) {
        this.systemPartners = systemPartners;
        if (this.systemPartners == null){
            this.systemPartners = new ArrayList<>();
        }
        this.systemPartners.add("IBM-SYSTEM");
    }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        Assert.notNull(token, "token cannot be null");
        if (token.getSubject().startsWith(SYSTEM_PARTNER)){
            if (CollectionUtils.isEmpty(systemPartners)){
                return OAuth2TokenValidatorResult.failure(NO_SYSTEM_PARTNER_PROVIDE);
            }else if(!systemPartners.contains(token.getSubject().split(":")[1])){
                return OAuth2TokenValidatorResult.failure(INVALID_SYSTEM_PARTNER);
            }
        }
        return OAuth2TokenValidatorResult.success();
    }
}
