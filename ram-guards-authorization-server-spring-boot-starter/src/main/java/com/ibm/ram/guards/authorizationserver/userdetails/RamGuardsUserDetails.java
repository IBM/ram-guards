package com.ibm.ram.guards.authorizationserver.userdetails;

import com.ibm.ram.guards.entity.RamGuardsAuthority;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;


/**
 * @author seanyu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class RamGuardsUserDetails extends RamGuardsUser implements UserDetails, CredentialsContainer, Serializable {

    private String password;
    private String username;
    private Collection<RamGuardsAuthority> authorities;
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;

    public RamGuardsUserDetails(RamGuardsUser ramGuardsUser){
        this.setUsername(ramGuardsUser.getUsername());
        this.setIsSystemPartner(ramGuardsUser.getIsSystemPartner());
        this.setDetails(ramGuardsUser.getDetails());
        this.setPassword("{noop}" + ramGuardsUser.getCredentials());
    }

    @Override
    public void eraseCredentials() {

    }

    @Override
    public Collection<RamGuardsAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
