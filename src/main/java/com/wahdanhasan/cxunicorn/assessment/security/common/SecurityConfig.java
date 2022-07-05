package com.wahdanhasan.cxunicorn.assessment.security.common;

import com.wahdanhasan.cxunicorn.assessment.exception.AuthenticationExceptionEntryPoint;
import com.wahdanhasan.cxunicorn.assessment.exception.CustomAccessDeniedHandler;
import com.wahdanhasan.cxunicorn.assessment.security.filter.AuthenticationFilter;
import com.wahdanhasan.cxunicorn.assessment.security.filter.AuthorizationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/* Security configuration.
*  Authorizes & Authenticates any request sent to the program
*
*  */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationExceptionEntryPoint authenticationExceptionEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Value("${jwt.expires.in}")
    private Integer jwtValidTime;

    @Value("${jwt.refresh.time}")
    private Integer jwtRefreshTime;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* Disable CSRF as it's not required */
        http.csrf().disable();

        /* No session will be created  */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /* Use a custom authentication filter to authenticate the user */
        http.addFilter(new AuthenticationFilter(authenticationManagerBean(), jwtValidTime, jwtRefreshTime, jwtIssuer));
        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        /* Set authentication entry point and access denied handler to custom implementations */
        http.exceptionHandling().authenticationEntryPoint(authenticationExceptionEntryPoint);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

        /* Permit access to log in and logout */
        http.authorizeRequests().antMatchers("/api/login/**").permitAll();
        http.authorizeRequests().antMatchers("/api/logout/**").permitAll();

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
