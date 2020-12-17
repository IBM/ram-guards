package com.ibm.ram.guards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.helper.RamGuardsJacksonHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author seanyu
 */
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new RamGuardsJacksonHelper.DeserializeObjectMapper();
    }
}
