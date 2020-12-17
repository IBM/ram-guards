package com.ibm.ram.guards.authorizationserver.provider;

import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUser;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author seanyu
 */
@Component
public interface RamGuardsAuthorityService {
    List<RamGuardsAuthority> getUserAuthorities(RamGuardsUser ramGuardsUser) throws Exception;
    List<RamGuardsAuthority> getSystemPartnerAuthorities(RamGuardsUser ramGuardsUser) throws Exception;

}
