package com.ibm.ibmid.client.oauth2;

import lombok.Getter;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public class IbmIdOidcToken extends DefaultOAuth2AccessToken {
    private Jwt idToken;
    public IbmIdOidcToken(OAuth2AccessToken accessToken,Jwt idToken) {
        super(accessToken);
        this.idToken = idToken;
    }
}
