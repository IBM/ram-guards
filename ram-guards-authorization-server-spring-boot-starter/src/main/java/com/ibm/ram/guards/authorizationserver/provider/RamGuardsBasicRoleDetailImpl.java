package com.ibm.ram.guards.authorizationserver.provider;

import com.ibm.ram.guards.entity.RamGuardsBasicRoleDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RamGuardsBasicRoleDetailImpl implements RamGuardsBasicRoleDetail {
    private int id;
    private String detail;
}
