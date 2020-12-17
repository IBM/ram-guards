package com.ibm.ram.guards.resourceserver.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import com.ibm.ram.guards.helper.JWEHelper;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.ibm.ram.guards.resourceserver.constant.RamGuardsAuthorityConstant.SYSTEM_PARTNER;
import static org.springframework.security.config.Elements.ANONYMOUS;

/**
 * @author seanyu
 */
public class RamGuardsRestTemplateHelper {
    public static HttpHeaders createRamGuardsHeaders(Authentication authentication, String userInfo){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authentication.getCredentials().toString());
        if (!StringUtils.isEmpty(userInfo)){
            headers.set("user-info", userInfo);
        }
        return headers;
    }
    public static HttpHeaders createRamGuardsHeaders(Authentication authentication){
        return createRamGuardsHeaders(authentication, null);
    }
    public static HttpHeaders createDefaultAnonymousRamGuardsHeaders(String clientId, String clientSecret){
        Map<String, Object> jweContent = new HashMap<>();
        jweContent.put("sub","ANONYMOUS");
        jweContent.put("user_name", "ANONYMOUS");
        jweContent.put("active", true);
        jweContent.put("application_id", "3");
        List<RamGuardsAuthority> ramGuardsAuthorities = new ArrayList<>();
        RamGuardsAuthority ramGuardsAuthority1 = new RamGuardsAuthority();
        ramGuardsAuthority1.setName(ANONYMOUS);
        ramGuardsAuthorities.add(ramGuardsAuthority1);
        RamGuardsAuthority ramGuardsAuthority2 = new RamGuardsAuthority();
        ramGuardsAuthority2.setName(SYSTEM_PARTNER);
        ramGuardsAuthorities.add(ramGuardsAuthority2);
        jweContent.put("authorities",ramGuardsAuthorities);
        jweContent.put("client_id", clientId);
        jweContent.put("aud", Collections.singletonList(clientId));
        jweContent.put("user_type", "ANONYMOUS");
        jweContent.put("grant_type","password");
        jweContent.put("system_partner","true");
        jweContent.put("scope", Collections.unmodifiableSet(new HashSet<>(Arrays.asList("authority","role","detail"))));
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("scopeFilter", SimpleBeanPropertyFilter.serializeAll()).setFailOnUnknownId(false);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setFilterProvider(filterProvider);
        String bearerToken = "";
        try {
            bearerToken = JWEHelper.generateJWEStringWithSymmetricKey(objectMapper.writeValueAsString(jweContent), clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);
        return headers;
    }

}
