package com.ibm.ram.guards.zuul.esw.provider.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.zuul.esw.provider.starter.RamGuardsAuthorizationServerProperties;
import com.ibm.ram.guards.zuul.esw.provider.starter.RamGuardsEswProviderZuulProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.RoutesEndpoint;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author seanyu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class RamGuardsEswProviderZuulFilter extends ZuulFilter {

    private RamGuardsAuthorizationServerProperties ramGuardsAuthorizationServerProperties;

    private RamGuardsEswProviderZuulProperties ramGuardsEswProviderZuulProperties;

    private RoutesEndpoint routesEndpoint;

    private HandlerMappingIntrospector handlerMappingIntrospector;

    private FilterChainProxy filterChainProxy;

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String url = request.getRequestURI();
        List<Filter> filters = filterChainProxy.getFilters(url);
        if (CollectionUtils.isEmpty(filters)){
            return null;
        }
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) request.getUserPrincipal();
        Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();
        String username = getEswProviderUsernameFromIbmIdToken(jwt);
        Map<String, String> additionalParameter = getAdditionalParameterFromIbmIdToken(jwt);
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, List<String>> newParameterMap = new HashMap<>();
        // getting the current parameter
        parameterMap.forEach((key, values) -> newParameterMap.put(key, Arrays.asList(values)));
        additionalParameter.forEach((key, values) -> newParameterMap.put(key, Collections.singletonList(values)));
        Map<String, String> routesEndpointMap = routesEndpoint.invoke();
        Map.Entry<String, String> authorizationRouteEntry = getAuthorizationRouteEntry(routesEndpointMap);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
            ctx.addZuulRequestHeader("user-info", objectMapper.writeValueAsString(jwt.getClaims()));
            ctx.addZuulRequestHeader("id-token", jwt.getTokenValue());
            if (new MvcRequestMatcher(handlerMappingIntrospector, authorizationRouteEntry.getKey()).matches(request)) {
                if (url.endsWith("token")){
                    makeAuthorizationServerTokenEndpointCall(ctx, newParameterMap, username, authorizationRouteEntry);
                }else if (url.endsWith("introspect")){
                    makeAuthorizationServerIntrospectEndpointCall(ctx, newParameterMap, username, authorizationRouteEntry);
                }
            } else {
                makeResourceServerCall(ctx, newParameterMap, username, routesEndpointMap);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ZuulException("user-info parsing exception", 500, "user-info parsing exception");
        }
        return null;
    }

    private String getEswProviderUsernameFromIbmIdToken(Jwt jwt) {
        String subject = jwt.getSubject();
        String username = (String) jwt.getClaims().get("preferred_username");
        if (jwt.getClaims().get("system_partner") != null && Boolean.parseBoolean(jwt.getClaims().get("system_partner").toString()) && "client_credentials".equals(jwt.getClaims().get("grant_type"))){
            return subject;
        }
        return username;
    }

    private Map<String, String> getAdditionalParameterFromIbmIdToken(Jwt jwt) {
        Map<String, String> additionalParameter = new HashMap<>();
        if (jwt.getClaims().get("system_partner") != null && Boolean.parseBoolean(jwt.getClaims().get("system_partner").toString())){
            additionalParameter.put("system_partner", "true");
            return additionalParameter;
        }
        String realmName = (String) jwt.getClaims().get("realmName");
        if (!realmName.contains("w3id")){
            additionalParameter.put("user_type", "WEBID");
        }else {
            additionalParameter.put("user_type", "IIP");
        }
        return additionalParameter;
    }

    private void makeAuthorizationServerTokenEndpointCall(RequestContext ctx, Map<String, List<String>> newParameterMap, String username, Map.Entry<String, String> authorizationRouteEntry) throws ZuulException {
        String auth = ramGuardsAuthorizationServerProperties.getClientId() + ":" + ramGuardsAuthorizationServerProperties.getClientSecret();
        String basicToken = new String(encodeBase64(auth.getBytes()));
        ctx.addZuulRequestHeader("Authorization", "Basic " + basicToken);
        if ("password".equals(newParameterMap.get("grant_type").get(0))) {
            log.info("user: {} request service: {} with token endpoint grant_type: {}.", username, authorizationRouteEntry.getValue(), "password");
            newParameterMap.put("username", Collections.singletonList(username));
            newParameterMap.put("password", Collections.singletonList(ramGuardsAuthorizationServerProperties.getDefaultPassword()));
            newParameterMap.put("scope", Collections.singletonList("authority role detail"));
        } else if ("refresh_token".equals(newParameterMap.get("grant_type").get(0))) {
            Assert.notNull(newParameterMap.get("refresh_token"), "refresh_token cannot be null");
            log.info("user: {} request service: {} with token endpoint grant_type: {}", username, authorizationRouteEntry.getValue(), "refresh_token");
        } else {
            throw new ZuulException("unsupported grant type", 400, "unsupported grant type");
        }
        ctx.setRequestQueryParams(newParameterMap);
    }

    private void makeAuthorizationServerIntrospectEndpointCall(RequestContext ctx, Map<String, List<String>> newParameterMap, String username, Map.Entry<String, String> authorizationRouteEntry) {
        String auth = ramGuardsAuthorizationServerProperties.getClientId() + ":" + ramGuardsAuthorizationServerProperties.getClientSecret();
        String basicToken = new String(encodeBase64(auth.getBytes()));
        ctx.addZuulRequestHeader("Authorization", "Basic " + basicToken);
        Assert.notNull(newParameterMap.get("token"), "token cannot be null");
        log.info("user: {} request service: {} with grant_type: {}.", username, authorizationRouteEntry.getValue(), "password");
        ctx.setRequestQueryParams(newParameterMap);
    }

    private void makeResourceServerCall(RequestContext ctx, Map<String, List<String>> newParameterMap, String username, Map<String, String> routesEndpointMap) {
        // add a new parameter
        HttpServletRequest request = ctx.getRequest();
        String roleToken = request.getHeader("Ram-Guards");
        ctx.addZuulRequestHeader("Authorization", "Bearer " + roleToken);
        newParameterMap.put("username", Collections.singletonList(username));
        routesEndpointMap.entrySet().stream()
                .filter(entry -> {
                    MvcRequestMatcher mvcRequestMatcher = new MvcRequestMatcher(handlerMappingIntrospector, entry.getKey());
                    return mvcRequestMatcher.matches(request);
                })
                .findFirst()
                .ifPresent(entry -> log.info("user: {} request service: {}", username, entry.getValue()));
        ctx.setRequestQueryParams(newParameterMap);
    }

    private Map.Entry<String, String> getAuthorizationRouteEntry(Map<String, String> routesEndpointMap) throws ZuulException {
        Map.Entry<String, String> authorizationRouteEntry = routesEndpointMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(ramGuardsEswProviderZuulProperties.getAuthorizationServerServiceId().trim()))
                .findFirst()
                .orElse(null);
        if (authorizationRouteEntry == null){
            throw new ZuulException("authorization service not found", 400, "authorization service not found");
        }
        return authorizationRouteEntry;
    }
}
