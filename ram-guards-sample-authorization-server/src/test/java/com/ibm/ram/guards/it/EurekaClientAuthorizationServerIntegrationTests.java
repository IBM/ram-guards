package com.ibm.ram.guards.it;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.ram.guards.helper.JWEHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EurekaClientAuthorizationServerIntegrationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Value("${ram-guards.authorization-server.client-id}")
    private String clientId;

    @Value("${ram-guards.authorization-server.client-secret}")
    private String clientSecret;

    private String username = "a728976009@gmail.com";

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
                                        .param("user_type","WEBID")
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
