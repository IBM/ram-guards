package com.ibm.ram.guards.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

/**
 * @author seanyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("scopeFilter")
public class RamGuardsAuthority implements GrantedAuthority, Serializable {

    private int id;
    private String name;
    private List<RamGuardsRole> roles;

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

}
