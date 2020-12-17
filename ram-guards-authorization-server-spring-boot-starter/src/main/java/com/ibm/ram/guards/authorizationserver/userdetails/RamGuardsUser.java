package com.ibm.ram.guards.authorizationserver.userdetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"username","isSystemPartner","details"})
public class RamGuardsUser implements Principal, Serializable {

    private String username;
    private Boolean isSystemPartner = false;
    // "user_type": "WEBID" or "user_type": "IIP"
    // "application_id": "1/2/3"
    // "become_user": "xxx"
    private Map<String, Object> details;
    private Object credentials;
    private Map<String, Object> credentialsDetails;

    @Override
    public String getName() {
        return username;
    }

    public String getRedisKey(){
        return username+":"+details.toString();
    }

}
