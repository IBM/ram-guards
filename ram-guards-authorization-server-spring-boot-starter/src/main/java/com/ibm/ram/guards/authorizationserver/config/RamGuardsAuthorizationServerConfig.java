package com.ibm.ram.guards.authorizationserver.config;

import com.ibm.ram.guards.authorizationserver.oauth2.converter.RamGuardsJweAccessTokenConverter;
import com.ibm.ram.guards.authorizationserver.oauth2.provider.RamGuardsUserDetailsAuthenticationProvider;
import com.ibm.ram.guards.authorizationserver.oauth2.validator.RamGuardsOAuth2RequestValidator;
import com.ibm.ram.guards.authorizationserver.starter.RamGuardsAuthorizationServerProperties;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsRefreshUserDetailsManager;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUser;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUserDetails;
import com.ibm.ram.guards.authorizationserver.userdetails.RamGuardsUserDetailsService;
import com.ibm.ram.guards.authorizationserver.web.filter.RamGuardsTokenEndpointFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_AUTHORITY;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_DETAIL;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2RequestScope.SCOPE_ROLE;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2SupportGrantType.PASSWORD;
import static com.ibm.ram.guards.authorizationserver.oauth2.RamGuardsOAuth2SupportGrantType.REFRESH_TOKEN;


/**
 * @author seanyu
 */
@Configuration
@EnableAuthorizationServer
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RamGuardsAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final @NonNull AuthenticationManager authenticationManagerBean;

    private final @NonNull RamGuardsRefreshUserDetailsManager refreshUserDetailsService;

    private final @NonNull RamGuardsAuthorizationServerProperties ramGuardsAuthorizationServerProperties;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(ramGuardsAuthorizationServerProperties.getClientId())
                .secret("{noop}" + ramGuardsAuthorizationServerProperties.getClientSecret())
                .authorizedGrantTypes(PASSWORD, REFRESH_TOKEN)
                .scopes(SCOPE_AUTHORITY, SCOPE_ROLE, SCOPE_DETAIL)
                .accessTokenValiditySeconds(ramGuardsAuthorizationServerProperties.getTokenTtl());

    }

    public DefaultTokenServices tokenServices() throws Exception {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(token -> {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) token.getPrincipal();
            RamGuardsUser ramGuardsUser = new RamGuardsUser();
            ramGuardsUser.setUsername(authenticationToken.getPrincipal().toString());
            Map<String, Object> details = (Map<String, Object>) authenticationToken.getDetails();
            if (details!=null){
                ramGuardsUser.setIsSystemPartner(Boolean.parseBoolean(details.get("system_partner").toString()));
            }
            ramGuardsUser.setDetails(details);
            ramGuardsUser.setCredentials(authenticationToken.getCredentials());
            return refreshUserDetailsService.loadUserByRamGuardsUser(ramGuardsUser);
        });
        defaultTokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenServices(tokenServices())
                .requestValidator(requestValidator())
                .authenticationManager(authenticationManagerBean);
    }


    @Bean
    public TokenStore tokenStore() throws Exception {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public RamGuardsOAuth2RequestValidator requestValidator() {
        return new RamGuardsOAuth2RequestValidator();
    }

    @Bean
    public RamGuardsJweAccessTokenConverter accessTokenConverter() throws Exception {
        final RamGuardsJweAccessTokenConverter converter = new RamGuardsJweAccessTokenConverter();
        converter.setAccessTokenConverter(converter);
        converter.setSymmetricKey(ramGuardsAuthorizationServerProperties.getClientSecret());
        return converter;
    }

}
