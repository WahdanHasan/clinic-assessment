package com.wahdanhasan.cxunicorn.assessment.security.filter;


import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.ResponseMessage;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Arrays.stream;

/* Custom authorization filter to authorize any API requests.
*  This filter ensures the incoming JWT is valid/not expired. It ensures the user who sent the JWT has the
*  required authorization to access a resource/API.
*
* */
public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /* If the user is trying to log in, allow him to do so.
        *  If he is attempting to access any API/resource, perform authorization */
        if (request.getServletPath().equals("/api/login")){
            filterChain.doFilter(request, response);
        }
        else {
            /* Get authorization from the request header */
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            /* Check if header is not null and contains the bearer token */
            if (authorizationHeader != null && authorizationHeader.startsWith(Constants.BEARER_PREFIX)) {
                try {
                    /* Obtain the bearer token without the bearer prefix */
                    String token = authorizationHeader.substring(Constants.BEARER_PREFIX.length());
                    /* Create algorithm to encode/decode the JWT */
                    Algorithm algo = Algorithm.HMAC256(Constants.SECRET_KEY);

                    /* Decode the JWT */
                    JWTVerifier verifier = JWT.require(algo).build();

                    /* Ensure the JWT is valid and hasn't been tampered with */
                    DecodedJWT decodedJWT = verifier.verify(token);

                    /* Get user principal and authorizations from JWT */
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim(Constants.CLAIM_ROLES).asArray(String.class);

                    /* Convert authorizations to spring recognized type */
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    if (roles != null)
                        stream(roles).forEach((role) -> {
                            authorities.add(new SimpleGrantedAuthority(role));
                        });

                    /* Create authentication token */
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,  null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    /* Add token to spring security context */
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    /* Continue with request */
                    filterChain.doFilter(request, response);
                }
                catch (IllegalArgumentException | TokenExpiredException | JWTDecodeException exception) {

                    /* Reply with a custom response format */
                    ResponseMessage message = new ResponseMessage(HttpStatus.FORBIDDEN.value(), exception.getMessage());

                    response.setStatus(HttpStatus.FORBIDDEN.value());;

                    response.setContentType(APPLICATION_JSON_VALUE);

                    response.getWriter()
                            .write(new ObjectMapper().writeValueAsString(new GenericResponseDto(message)));
                }
            }
            else {
                filterChain.doFilter(request, response);
            }
        }

    }
}
