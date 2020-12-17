package com.ibm.ram.guards;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import com.ibm.ram.guards.entity.RamGuardsRole;
import com.ibm.ram.guards.resourceserver.oauth2.token.RamGuardsJweAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author seanyu
 */
@RestController
public class EurekaClientController {

    @Value("${server.port}")
    String port;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/hi")
    public Map<String, Object> home(RamGuardsJweAuthenticationToken authentication, @RequestHeader(value = "user-info", required = false) String userInfo, @RequestHeader(value = "id-token", required = false) String idToken) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Collection<RamGuardsAuthority> ramAuthorities = (Collection<RamGuardsAuthority>) (Collection<?>) authentication.getAuthorities();
        response.put("principal", authentication.getPrincipal());
        response.put("credentials", authentication.getCredentials());
        response.put("claims", authentication.getTokenAttributes());
        // String becomeUser = (String) authentication.getTokenAttributes().get("become_user");
        response.put("authorities", ramAuthorities);
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        Map<String,Object> userInfoMap = new ObjectMapper().readValue(userInfo, typeRef);
        response.put("userInfos", userInfoMap);
        response.put("idToken", idToken);

        return response;
    }

    @GetMapping("/service-instances/{applicationName}")
    public @ResponseBody List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    @GetMapping("/service-instances/getAuthentication")
    public @ResponseBody Authentication serviceInstancesByApplicationName(Authentication authentication) {
        return authentication;
    }

    @GetMapping("/check/authority/{authority}")
    public ResponseEntity checkAuthority(@PathVariable String authority, Authentication authentication){
        Collection<RamGuardsAuthority> ramAuthorities = (Collection<RamGuardsAuthority>) authentication.getAuthorities();
        if (authority != null){
            List<RamGuardsAuthority> hasAuthorityList = ramAuthorities.stream()
                    .filter(ramGuardsAuthority -> authority.equals(ramGuardsAuthority.getAuthority()))
                    .collect(Collectors.toList());
            if (hasAuthorityList.size() != 0){
                return new ResponseEntity<>(hasAuthorityList, HttpStatus.OK);
            }else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

        }
        return null;
    }

    @GetMapping("/check/role/{role}")
    public ResponseEntity checkRole(@PathVariable String role, Authentication authentication){
        Collection<RamGuardsAuthority> ramAuthorities = (Collection<RamGuardsAuthority>) authentication.getAuthorities();
        if (role != null){
            List<RamGuardsRole> hasRoleList = ramAuthorities.stream()
                    .flatMap(ramGuardsAuthority -> ramGuardsAuthority.getRoles().stream())
                    .filter(ramGuardsRole -> role.equals(ramGuardsRole.getName()))
                    .collect(Collectors.toList());
            if (hasRoleList.size() != 0){
                return new ResponseEntity<>(hasRoleList, HttpStatus.OK);
            }else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

        }
        return null;
    }


}
