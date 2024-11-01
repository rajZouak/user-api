package com.example.userapi.security;

import com.example.userapi.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApiSecurityConfiguration {

    private final JwtTokenFilter jtwTokenFilter;
    private final AuthenticationService authService;
    private final PasswordEncoder passwordEncoder;

    public ApiSecurityConfiguration(JwtTokenFilter jtwTokenFilter, AuthenticationService authService, PasswordEncoder passwordEncoder) {
        this.jtwTokenFilter = jtwTokenFilter;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().headers()
                .frameOptions().disable().and()
                .authorizeRequests()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/auth", "/api/users/generate", "/api/users/batch").permitAll()
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/users/**").hasRole("admin")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(jtwTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}