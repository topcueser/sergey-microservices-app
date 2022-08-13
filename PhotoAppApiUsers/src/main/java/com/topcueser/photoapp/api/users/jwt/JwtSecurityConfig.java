package com.topcueser.photoapp.api.users.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        JwtAuthenticationFilter customAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider, authenticationManager);
        JwtAuthorizationFilter customAuthorizationFilter = new JwtAuthorizationFilter(tokenProvider);

        builder
            .addFilter(customAuthenticationFilter)
            .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
