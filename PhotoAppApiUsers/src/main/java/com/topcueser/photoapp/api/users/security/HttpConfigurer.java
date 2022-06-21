package com.topcueser.photoapp.api.users.security;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.topcueser.photoapp.api.users.ui.service.UsersService;

public class HttpConfigurer extends AbstractHttpConfigurer<HttpConfigurer, HttpSecurity>{

	private final Environment environment;
	private final UsersService usersService;
	
	public HttpConfigurer(Environment environment, UsersService usersService) {
		this.environment = environment;
		this.usersService = usersService;
	}
	
	
	@Override
    public void init(HttpSecurity builder) throws Exception {
        builder
                .cors().and().csrf().disable() // json uzerinden post gonderebilmek icin disable ediyoruz
                .authorizeRequests((auth) -> {
                    //auth.anyRequest().authenticated();
                    auth.antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"));
                })
                .headers().frameOptions().disable().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        builder
                .addFilter(new JwtAuthenticationFilter(authenticationManager, usersService, environment));
    }
}
