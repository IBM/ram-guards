package com.ibm.ram.guards.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * @author seanyu
 */
public class RamGuardsJacksonHelper  {
    public static class DeserializeObjectMapper extends ObjectMapper{
        public DeserializeObjectMapper(){
            super();
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("scopeFilter", SimpleBeanPropertyFilter.serializeAll()).setFailOnUnknownId(false);
            this.setFilterProvider(filterProvider);
        }

        @Override
        public ObjectMapper copy() {
            return this;
        }
    }
}
