package com.topcueser.photoapp.api.users.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcueser.photoapp.api.users.shared.UserDto;
import com.topcueser.photoapp.api.users.ui.model.LoginRequestModel;
import com.topcueser.photoapp.api.users.ui.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Environment environment;
    private final UsersService usersService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, Environment environment, UsersService usersService) {
        this.authenticationManager = authenticationManager;
        this.environment = environment;
        this.usersService = usersService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {

            LoginRequestModel loginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequestModel.getEmail(), loginRequestModel.getPassword());
            return authenticationManager.authenticate(token);

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException, IOException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        Algorithm algorithm = Algorithm.HMAC256(Objects.requireNonNull(environment.getProperty("token.secret")).getBytes());

        UserDto usersDetails = usersService.getUserDetailsByEmail(username);

        String accessToken = JWT.create()
                .withSubject(usersDetails.getUserId())
                .withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), Map.of("access_token", accessToken, "user_id", usersDetails.getUserId()));
    }

}
