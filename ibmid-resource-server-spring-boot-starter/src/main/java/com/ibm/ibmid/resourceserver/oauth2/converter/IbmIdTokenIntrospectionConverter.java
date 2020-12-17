package com.ibm.ibmid.resourceserver.oauth2.converter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.*;

import static org.springframework.security.oauth2.jwt.JwtClaimNames.SUB;

/**
 * @author seanyu
 */
public class IbmIdTokenIntrospectionConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<>();
        Set<String> scope = extractScope(map);
        Authentication user = new UsernamePasswordAuthenticationToken(map.get(SUB), "N/A", null);
        String clientId = (String) map.get(CLIENT_ID);
        Boolean active = (Boolean) map.get("active");
        Integer iat = (Integer) map.get("iat");
        Integer exp = (Integer) map.get("exp");

        parameters = (Map<String, String>) map;

        parameters.put("active", String.valueOf(active));
        parameters.put("iat", String.valueOf(iat));
        parameters.put("exp", String.valueOf(exp));

        Set<String> resourceIds = Collections.emptySet();

        OAuth2Request request = new OAuth2Request(parameters, clientId, null, true, scope, resourceIds, null, null,
                null);
        return new OAuth2Authentication(request, user);
    }

    private Set<String> extractScope(Map<String, ?> map) {
        Set<String> scope = Collections.emptySet();
        if (map.containsKey(SCOPE)) {
            Object scopeObj = map.get(SCOPE);
            if (scopeObj instanceof String) {
                scope = new LinkedHashSet<>(Arrays.asList(((String) scopeObj).split(" ")));
            } else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
                Collection<String> scopeColl = (Collection<String>) scopeObj;
                scope = new LinkedHashSet<>(scopeColl);
            }
        }
        return scope;
    }

}
