package com.topcueser.photoapp.api.users.security;

import com.topcueser.photoapp.api.users.ui.service.UsersService;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

public class HttpConfigurer extends AbstractHttpConfigurer<HttpConfigurer, HttpSecurity>{

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/users/**").permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        UsersService usersService = builder.getSharedObject(UsersService.class);
        Environment environment = builder.getSharedObject(Environment.class);
        builder.addFilter(new JwtAuthenticationFilter(authenticationManager, usersService, environment));
    }


    public static HttpConfigurer httpConfigurer() {
        return new HttpConfigurer();
    }
}
