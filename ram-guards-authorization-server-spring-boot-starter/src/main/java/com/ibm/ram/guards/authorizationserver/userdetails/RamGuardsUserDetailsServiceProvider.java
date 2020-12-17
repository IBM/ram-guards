package com.ibm.ram.guards.authorizationserver.userdetails;

import com.ibm.ram.guards.authorizationserver.provider.RamGuardsAuthorityService;
import com.ibm.ram.guards.authorizationserver.starter.RamGuardsAuthorizationServerProperties;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Elements.ANONYMOUS;

/**
 * @author seanyu
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsUserDetailsServiceProvider {

    private static final String SYSTEM_PARTNER = "SYSTEM_PARTNER";

    private static final String DEFAULT_SYSTEM_PARTNER_NAME = "IBM-SYSTEM";

    private final @NonNull RamGuardsAuthorityService ramGuardsAuthorityService;

    private final @NonNull RamGuardsAuthorizationServerProperties ramGuardsAuthorizationServerProperties;

    public UserDetails loadUserByRamGuardsUser(RamGuardsUser ramGuardsUser) throws Exception {
        List<RamGuardsAuthority> ramGuardsAuthorities;
        if (ramGuardsUser.getIsSystemPartner() != null && ramGuardsUser.getIsSystemPartner()){
            String clientIdOrUsername = ramGuardsUser.getUsername();
            if (clientIdOrUsername.equals(DEFAULT_SYSTEM_PARTNER_NAME)){
                ramGuardsAuthorities = new ArrayList<>();
                RamGuardsAuthority ramGuardsAuthority = new RamGuardsAuthority();
                ramGuardsAuthority.setName(ANONYMOUS);
                ramGuardsAuthorities.add(ramGuardsAuthority);
            }else {
                if(CollectionUtils.isEmpty(ramGuardsAuthorizationServerProperties.getSystemPartners())){
                    throw new Exception("ram guards authorization server don't have any system partner");
                }
                if(!ramGuardsAuthorizationServerProperties.getSystemPartners().contains(clientIdOrUsername)){
                    throw new Exception("Invalid System-Partner: " + clientIdOrUsername);
                }
                ramGuardsAuthorities = ramGuardsAuthorityService.getSystemPartnerAuthorities(ramGuardsUser);
            }
            RamGuardsAuthority ramGuardsAuthority = new RamGuardsAuthority();
            ramGuardsAuthority.setName(SYSTEM_PARTNER);
            ramGuardsAuthorities.add(ramGuardsAuthority);
        }else {
            ramGuardsAuthorities = ramGuardsAuthorityService.getUserAuthorities(ramGuardsUser);
            if (CollectionUtils.isEmpty(ramGuardsAuthorities)){
                RamGuardsAuthority ramGuardsAuthority = new RamGuardsAuthority();
                ramGuardsAuthority.setName(ANONYMOUS);
                ramGuardsAuthorities = Collections.singletonList(ramGuardsAuthority);
            }
        }
        RamGuardsUserDetails ramGuardsUserDetails = new RamGuardsUserDetails(ramGuardsUser);
        ramGuardsUserDetails.setAuthorities(ramGuardsAuthorities);
        return ramGuardsUserDetails;
    }
}
