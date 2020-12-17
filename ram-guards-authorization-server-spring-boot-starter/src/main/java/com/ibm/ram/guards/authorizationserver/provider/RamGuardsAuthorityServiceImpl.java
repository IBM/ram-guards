package com.ibm.ram.guards.authorizationserver.provider;

import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUser;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import com.ibm.ram.guards.entity.RamGuardsBasicRoleDetail;
import com.ibm.ram.guards.entity.RamGuardsRole;
import lombok.Data;

import java.util.Arrays;
import java.util.List;


@Data
public class RamGuardsAuthorityServiceImpl implements RamGuardsAuthorityService {

    RamGuardsBasicRoleDetail ramGuardsBasicRoleDetail = new RamGuardsBasicRoleDetailImpl(1,"details");

    RamGuardsRole ramGuardsRole = new RamGuardsRole(1, "role", Arrays.asList(ramGuardsBasicRoleDetail));

    RamGuardsAuthority ramGuardsAuthority = new RamGuardsAuthority(1, "authority", Arrays.asList(ramGuardsRole));

    RamGuardsAuthority ramGuardsSystemPartnerAuthority = new RamGuardsAuthority(1, "SystemPartner-authority", Arrays.asList(ramGuardsRole));

    @Override
    public List<RamGuardsAuthority> getUserAuthorities(RamGuardsUser ramGuardsUser) throws Exception {
        return Arrays.asList(ramGuardsAuthority);
    }

    @Override
    public List<RamGuardsAuthority> getSystemPartnerAuthorities(RamGuardsUser ramGuardsUser) throws Exception {
        return Arrays.asList(ramGuardsSystemPartnerAuthority);
    }
}
