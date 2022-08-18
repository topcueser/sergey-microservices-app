package com.topcueser.photoapp.api.users.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.topcueser.photoapp.api.users.shared.UserDto;
import com.topcueser.photoapp.api.users.services.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TokenProvider implements InitializingBean {

    private final String secret;
    private final Long tokenValidityInMilliseconds;
    private final UserService userService;
    private Algorithm algorithm;

    public TokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds, UserService userService) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
        this.userService = userService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.algorithm = Algorithm.HMAC256(Objects.requireNonNull(secret).getBytes());
    }

    public String createToken(HttpServletRequest request, Authentication authResult) {

        User user = (User) authResult.getPrincipal();
        UserDto usersDetails = userService.getUserDetailsByEmail(user.getUsername());

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return JWT.create()
            .withSubject(usersDetails.getUserId())
            .withExpiresAt(validity)
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(algorithm);
    }

    public Authentication getAuthentication(String token) {

        JWTVerifier jwtVerifier = JWT.require(this.algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        List<GrantedAuthority> authorities =
                Stream.of(decodedJWT.getClaim("roles").asArray(String.class)).map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(decodedJWT.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
