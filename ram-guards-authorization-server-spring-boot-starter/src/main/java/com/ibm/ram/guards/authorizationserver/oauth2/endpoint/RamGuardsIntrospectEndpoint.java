package com.ibm.ram.guards.authorizationserver.oauth2.endpoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FrameworkEndpoint
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsIntrospectEndpoint {

    @NonNull
    private final CheckTokenEndpoint checkToken;

    @GetMapping(value = "/oauth/introspect")
    @ResponseBody
    public Map<String, ?> introspect(@RequestParam("token") String value){
        return checkToken.checkToken(value);
    }
}
