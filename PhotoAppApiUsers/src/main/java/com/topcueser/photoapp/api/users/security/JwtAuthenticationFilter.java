package com.topcueser.photoapp.api.users.security;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcueser.photoapp.api.users.shared.UserDto;
import com.topcueser.photoapp.api.users.ui.model.LoginRequestModel;
import com.topcueser.photoapp.api.users.ui.service.UsersService;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	private final UsersService usersService;
	private final Environment environment;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UsersService usersService, Environment environment) {
		this.authenticationManager = authenticationManager;
		this.usersService = usersService;
		this.environment = environment;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			
			LoginRequestModel loginRequest = new ObjectMapper()
					.readValue(request.getInputStream(), LoginRequestModel.class);
			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), new ArrayList<>());
			return authenticationManager.authenticate(token);
			
		} catch (IOException e) {
			throw new RuntimeException();
		}

	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String username = ((User) authResult.getPrincipal()).getUsername();
		UserDto userDetails = usersService.getUserDetailsByEmail(username);
        Algorithm algorithm = Algorithm.HMAC256(environment.getProperty("token.secret").getBytes());

        String accessToken = JWT.create()
                .withSubject(userDetails.getUserId())
                .withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), Map.of("access_token", accessToken));
        
        //response.addHeader("token", accessToken);
        //response.addHeader("userId", userDetails.getUserId());
		
	}
}
