package com.ibm.ram.guards.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author seanyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("scopeFilter")
public class RamGuardsRole implements Serializable {

    private int id;
    private String name;
    private List<? extends RamGuardsBasicRoleDetail> details;

}
