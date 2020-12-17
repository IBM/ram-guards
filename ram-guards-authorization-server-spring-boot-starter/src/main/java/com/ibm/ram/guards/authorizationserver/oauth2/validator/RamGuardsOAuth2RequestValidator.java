package com.ibm.ram.guards.authorizationserver.oauth2.validator;

import com.google.common.collect.Sets;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.TokenRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_AUTHORITY;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_DETAIL;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_ROLE;


/**
 * @author seanyu
 */
public class RamGuardsOAuth2RequestValidator implements OAuth2RequestValidator {

    @Override
    public void validateScope(AuthorizationRequest authorizationRequest, ClientDetails client) throws InvalidScopeException {
        validateScope(authorizationRequest.getScope(), client.getScope());
    }

    @Override
    public void validateScope(TokenRequest tokenRequest, ClientDetails client) throws InvalidScopeException {
        validateScope(tokenRequest.getScope(), client.getScope());
    }

    private void validateScope(Set<String> requestScopes, Set<String> clientScopes) {

        if (clientScopes != null && !clientScopes.isEmpty()) {
            for (String scope : requestScopes) {
                if (!clientScopes.contains(scope)) {
                    throw new InvalidScopeException("Invalid scope: " + scope, clientScopes);
                }
            }
        }

        if (requestScopes.isEmpty()) {
            throw new InvalidScopeException("Empty scope (either the client or the user is not allowed the requested scopes)");
        }

        boolean threeScopes = Sets.symmetricDifference(requestScopes, new HashSet<>(Arrays.asList(SCOPE_AUTHORITY,SCOPE_ROLE,SCOPE_DETAIL))).size() == 0;
        boolean twoScopes = Sets.symmetricDifference(requestScopes, new HashSet<>(Arrays.asList(SCOPE_AUTHORITY,SCOPE_ROLE))).size() == 0;
        boolean oneScope = Sets.symmetricDifference(requestScopes, new HashSet<>(Arrays.asList(SCOPE_AUTHORITY))).size() == 0;

        if (!(threeScopes || twoScopes || oneScope))
        {
            throw new InvalidScopeException("Invalid scope!");
        }
    }
}
