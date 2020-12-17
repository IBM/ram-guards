package com.ibm.ibmid;

import com.ibm.ibmid.client.oauth2.IbmIdOidcToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/token")
    @ResponseBody
    public Map<String, String> getToken(Authentication authentication){
        IbmIdOidcToken ibmIdOidcToken = (IbmIdOidcToken) authentication.getCredentials();
        Map<String, String> map = new HashMap<>();
        map.put("accesstoken", ibmIdOidcToken.getValue());
        map.put("refreshtoken", ibmIdOidcToken.getRefreshToken().getValue());
        map.put("idtoken", ibmIdOidcToken.getIdToken().getTokenValue());
        return map;
    }
}
