package com.ibm.ram.guards.authorizationserver.oauth2.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Sets;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUserDetails;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import com.ibm.ram.guards.entity.RamGuardsRole;
import com.ibm.ram.guards.helper.JWEHelper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.Elements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_AUTHORITY;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_DETAIL;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_ROLE;
import static org.springframework.security.oauth2.provider.token.UserAuthenticationConverter.USERNAME;


/**
 * @author seanyu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class RamGuardsJweAccessTokenConverter extends JwtAccessTokenConverter {

    private String symmetricKey;

    private static final String SUB = "sub";

    private static final String IAT = "iat";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonParser jsonParser = JsonParserFactory.create();

    private final DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    public RamGuardsJweAccessTokenConverter(){
        this.tokenConverter.setUserTokenConverter(new RamGuardsUserAuthenticationConverter());
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        result.setValue(encode(result, authentication));
        OAuth2RefreshToken refreshToken = result.getRefreshToken();
        if (refreshToken != null) {
            DefaultOAuth2AccessToken encodedRefreshToken = new DefaultOAuth2AccessToken(accessToken);
            encodedRefreshToken.setValue(refreshToken.getValue());
            // Refresh tokens do not expire unless explicitly of the right type
            encodedRefreshToken.setExpiration(null);
            DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken(encode(encodedRefreshToken, authentication));
            result.setRefreshToken(token);
        }
        return result;
    }

    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, ?> jweContent = convertAccessToken(accessToken, authentication);
        String content = jsonParser.formatMap(jweContent);
        if ("password".equals(jweContent.get("grant_type"))){
            log.info("user: {} authorized success with access token info: {}", jweContent.get("sub"), content);
        }else if("refresh_token".equals(jweContent.get("grant_type"))){
            log.info("user: {} authorized success with refresh token info: {}", jweContent.get("sub"), content);
        }
        try {
            return JWEHelper.generateJWEStringWithSymmetricKey(content, symmetricKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Map<String, Object> decode(String token) {
        try {
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
            Map<String, Object> claims = objectMapper.readValue(JWEHelper.decryptJWEPayloadWithCekOrSymmetricKey(token, symmetricKey), typeRef);
            if (claims.containsKey(EXP) && claims.get(EXP) instanceof Integer) {
                Integer intValue = (Integer) claims.get(EXP);
                claims.put(EXP, new Long(intValue));
            }
            this.getJwtClaimsSetVerifier().verify(claims);
            return claims;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> response = new HashMap<>();
        if (token.getExpiration() != null){
            // represent this token is an access token
            response.putAll(extractDetails(((RamGuardsUserDetails)authentication.getUserAuthentication().getPrincipal()).getDetails()));
            response.put("system_partner", ((RamGuardsUserDetails)authentication.getUserAuthentication().getPrincipal()).getIsSystemPartner());
            response.put(EXP, token.getExpiration().getTime() / 1000);
            response.put(IAT, token.getExpiration().getTime() / 1000 - token.getExpiresIn() - 1);
            response.put(AUD, authentication.getOAuth2Request().getClientId());
            response.put(SUB, authentication.getName());
            response.put(GRANT_TYPE, "password");
            response.put(JTI, uuid);
            Set<String> scopes =  token.getScope();
            response.put(SCOPE, scopes);
            response.put(CLIENT_ID, authentication.getOAuth2Request().getClientId());
            response.put(USERNAME, authentication.getName());
            // AbstractAuthenticationToken.class sucks, forces me cast this way
            Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
            List<RamGuardsAuthority> ramAuthorities = new ArrayList<>((Collection<RamGuardsAuthority>) grantedAuthorities);
            FilterProvider filterProvider = null;
            if (Sets.symmetricDifference(scopes, new HashSet<>(Arrays.asList(SCOPE_AUTHORITY,SCOPE_ROLE,SCOPE_DETAIL))).size() == 0){
                filterProvider = new SimpleFilterProvider().addFilter("scopeFilter", SimpleBeanPropertyFilter.serializeAll()).setFailOnUnknownId(false);
            }else if (Sets.symmetricDifference(scopes, new HashSet<>(Arrays.asList(SCOPE_AUTHORITY,SCOPE_ROLE))).size() == 0){
                filterProvider = new SimpleFilterProvider().addFilter("scopeFilter", SimpleBeanPropertyFilter.serializeAllExcept("details")).setFailOnUnknownId(false);
            }else if (Sets.symmetricDifference(scopes, new HashSet<>(Arrays.asList(SCOPE_AUTHORITY))).size() == 0){
                filterProvider = new SimpleFilterProvider().addFilter("scopeFilter", SimpleBeanPropertyFilter.serializeAllExcept("roles")).setFailOnUnknownId(false);
            }else {
                // check before, do nothing
            }
            objectMapper.setFilterProvider(filterProvider);
            // see https://github.com/FasterXML/jackson-databind/issues/984
            response.put(UserAuthenticationConverter.AUTHORITIES, objectMapper.convertValue(ramAuthorities, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class)));
        }else {
            // represent this token is a refresh token
            response.putAll(extractDetails(((RamGuardsUserDetails)authentication.getUserAuthentication().getPrincipal()).getDetails()));
            response.put("system_partner", ((RamGuardsUserDetails)authentication.getUserAuthentication().getPrincipal()).getIsSystemPartner());
            response.put(AUD, authentication.getOAuth2Request().getClientId());
            response.put(SUB, authentication.getName());
            response.put(GRANT_TYPE, "refresh_token");
            response.put(ATI, uuid);
            response.put(SCOPE, token.getScope());
            response.put(CLIENT_ID, authentication.getOAuth2Request().getClientId());
            response.put(USERNAME, authentication.getName());
        }
        return response;
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        return this.tokenConverter.extractAccessToken(value, map);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication authentication = this.tokenConverter.extractAuthentication(map);
        authentication.setDetails(extractDetails(map));
        return authentication;
    }

    public Map<String, ?> extractDetails(Map<String, ?> map){
        map.remove(EXP);
        map.remove(IAT);
        map.remove(AUD);
        map.remove(SUB);
        map.remove(GRANT_TYPE);
        map.remove(JTI);
        map.remove(ATI);
        map.remove(SCOPE);
        map.remove(CLIENT_ID);
        map.remove(USERNAME);
        map.remove(AUTHORITIES);
        map.remove("username");
        return map;
    }

}
