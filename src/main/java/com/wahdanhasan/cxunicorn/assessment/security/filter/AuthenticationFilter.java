package com.wahdanhasan.cxunicorn.assessment.security.filter;

import com.wahdanhasan.cxunicorn.assessment.util.Constants;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/* Custom authentication filter to authenticate user credentials.
*
*
* */

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private Integer jwtValidTime;
    private Integer jwtRefreshTime;
    private String jwtIssuer;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Integer jwtValidTime, Integer jwtRefreshTime, String jwtIssuer) {
        this.authenticationManager = authenticationManager;
        this.jwtValidTime = jwtValidTime;
        this.jwtRefreshTime = jwtRefreshTime;
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        /* This method is called when the user attempts to be authenticated */

        /* Get the user's username and password from the request */
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        /* Create an authentication token for the user based on the credentials he has provided */
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        /* Attempt to authenticate the user */
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        /* This method is called after a successful authentication */

        /* Create a reference to the authenticated user */
        User user = (User) authResult.getPrincipal();

        /* Create a token algorithm based on the secret key */
        Algorithm tokenAlgo = Algorithm.HMAC256(Constants.SECRET_KEY);

        /* Create the access token using the token algorithm */
        Date accessTokenExpiry = new Date(System.currentTimeMillis() + jwtValidTime);
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + jwtValidTime + jwtRefreshTime);

        /* Generate the access token */
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessTokenExpiry)
                .withIssuer(jwtIssuer)
                .withClaim(Constants.CLAIM_ROLES, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(tokenAlgo);

        /* Generate the refresh token */
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshTokenExpiry)
                .withIssuer(jwtIssuer)
                .sign(tokenAlgo);

        /* Set the access and refresh tokens in the response JSON */
        response.setContentType(APPLICATION_JSON_VALUE);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
