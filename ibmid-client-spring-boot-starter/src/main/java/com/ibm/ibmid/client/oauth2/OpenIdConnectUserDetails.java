package com.ibm.ibmid.client.oauth2;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class OpenIdConnectUserDetails implements UserDetails {

    private static final long serialVersionUID = -4153740588312462691L;

    private String userId;
    private String email;
    private String preferredUsername;
    private String ibmUniqueId;
    private OAuth2AccessToken accessToken;
    private Jwt idToken;

    OpenIdConnectUserDetails(Map<String, Object> userInfo, OAuth2AccessToken accessToken, Jwt idToken) {
        this.userId = (String) userInfo.get("sub");
        this.email = (String) userInfo.get("email");
        this.ibmUniqueId = (String) userInfo.get("uniqueSecurityName");
        this.preferredUsername = (String) userInfo.get("preferred_username");
        this.accessToken = accessToken;
        this.idToken = idToken;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("preferredUsername", preferredUsername)
                .add("email", email)
                .add("accessToken", accessToken)
                .toString();
    }
}
