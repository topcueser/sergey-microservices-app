package com.topcueser.photoapp.api.users.security;

import com.topcueser.photoapp.api.users.ui.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

public class HttpConfigurer extends AbstractHttpConfigurer<HttpConfigurer, HttpSecurity>{

    private final Environment environment;
    private final UsersService usersService;

    public HttpConfigurer(Environment environment, UsersService usersService) {
        this.environment = environment;
        this.usersService = usersService;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST ,"/users").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/users/login")
                .usernameParameter("email")
                .permitAll()
                .and()
                .logout().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().headers().frameOptions().sameOrigin();
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        builder.addFilter(new JwtAuthenticationFilter(authenticationManager, environment, usersService));
    }

    public static HttpConfigurer httpConfigurer(Environment environment, UsersService usersService) {
        return new HttpConfigurer(environment, usersService);
    }
}
