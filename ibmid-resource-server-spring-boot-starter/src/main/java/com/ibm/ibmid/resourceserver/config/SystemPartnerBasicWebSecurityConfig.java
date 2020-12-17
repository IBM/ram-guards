//package com.ibm.ibmid.resourceserver.config;
//
//import com.ibm.ibmid.resourceserver.basic.SystemPartnerBasicAuthenticationEntryPoint;
//import com.ibm.ibmid.resourceserver.basic.SystemPartnerBasicAuthenticationProvider;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Order(2)
//public class SystemPartnerBasicWebSecurityConfig extends AbstractHttpConfigurer<SystemPartnerBasicWebSecurityConfig, HttpSecurity> {
//
//    private final @NonNull SystemPartnerBasicAuthenticationEntryPoint authenticationEntryPoint;
//
//    @Override
//    public void init(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf()
//                .disable()
//                .httpBasic()
//                .authenticationEntryPoint(authenticationEntryPoint);
//    }
//
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("xxxx")
//                .password(passwordEncoder.encode("xxxx"))
//                .authorities("SYSTEM_PARTNER");
//        SystemPartnerBasicAuthenticationProvider provider = new SystemPartnerBasicAuthenticationProvider();
//        provider.setUserDetailsService(auth.getDefaultUserDetailsService());
//        provider.setPasswordEncoder(passwordEncoder);
//        auth.authenticationProvider(provider);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//}
//
