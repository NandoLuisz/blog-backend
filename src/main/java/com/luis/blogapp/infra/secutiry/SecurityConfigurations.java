package com.luis.blogapp.infra.secutiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    SecutiryFilter secutiryFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login-creator").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register-creator").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/creator/all-creators/{creatorId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/creator/all-creators").permitAll()
                        .requestMatchers(HttpMethod.POST, "/creator/updateFileDefaultCreator").permitAll()
                        .requestMatchers(HttpMethod.POST, "/post/create-post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/all-posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/all-post/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/last-post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/last-post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/all-posts-by-creator/{createdId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/post/delete-post/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/aws/s3/delete").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/post/delete-post/{id}").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/post/last-post").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/post/all-posts").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/post/create-post").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/auth/login-creator").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/auth/register-creator").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/post/all-posts-by-creator/{createdId}").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(secutiryFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
