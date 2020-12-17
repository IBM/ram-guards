package com.ibm.ram.guards.ut;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.authorizationserver.provider.RamGuardsAuthorityService;
import com.ibm.ram.guards.authorizationserver.starter.RamGuardsAuthorizationServerAutoConfig;
import com.ibm.ram.guards.entity.RamGuardsAuthority;
import com.ibm.ram.guards.helper.JWEHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {RamGuardsAuthorizationServerAutoConfig.class})
public class EurekaClientAuthorizationServerUnitTests {

    @MockBean
    private RamGuardsAuthorityService ramGuardsAuthorityService;

    @Before
    public void setRamGuardsAuthorityService() throws Exception {
        RamGuardsAuthority ramGuardsAuthority = new RamGuardsAuthority();
        ramGuardsAuthority.setName("SYSTEM_PARTNER");
        when(ramGuardsAuthorityService.getUserAuthorities(any())).thenReturn(Collections.singletonList(ramGuardsAuthority));
    }

    @Autowired
    private MockMvc mockMvc;

    @Value("${ram-guards.authorization-server.client-id}")
    private String clientId;

    @Value("${ram-guards.authorization-server.client-secret}")
    private String clientSecret;

    private String username = "SYSTEM_PARTNER:OWNiNzU1OTctMzAwOC00";

    private String refreshToken;

    @Test
    public void getAuthorizationTokenAndRefreshAccessToken() throws Exception {
        String responseBody =
                this.mockMvc
                        .perform(
                                post("/oauth/token")
                                        .param("scope","authority role detail")
                                        .param("grant_type", "password")
                                        .param("username", username)
                                        .param("password", "default-password")
                                        .header("Authorization", "Basic " + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes())))
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        HashMap<String,String> responseBodyMap = mapper.readValue(responseBody, typeRef);
        String accessToken = responseBodyMap.get("access_token");
        String jwePayload = JWEHelper.decryptJWEPayloadWithCekOrSymmetricKey(accessToken, clientSecret);
        System.out.println(jwePayload);
        Assert.assertTrue(jwePayload.contains(username));
        refreshToken = responseBodyMap.get("refresh_token");
    }

    @Test
    public void refreshAccessToken() throws Exception {
        getAuthorizationTokenAndRefreshAccessToken();
        String responseBody =
                this.mockMvc
                        .perform(
                                post("/oauth/token")
                                        .param("scope","authority role detail")
                                        .param("grant_type", "refresh_token")
                                        .param("refresh_token", refreshToken)
                                        .header("Authorization", "Basic " + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes())))
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        HashMap<String,String> responseBodyMap = mapper.readValue(responseBody, typeRef);
        String accessToken = responseBodyMap.get("access_token");
        String jwePayload = JWEHelper.decryptJWEPayloadWithCekOrSymmetricKey(accessToken, clientSecret);
        System.out.println(jwePayload);
        Assert.assertTrue(jwePayload.contains(username));
    }

}
