package com.banana.AccountsService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class ResourceServerConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/accounts/**").authenticated()
                .mvcMatchers("/accounts/{id}/owner/{ownerId}/addBalance", "/accounts/{id}/owner/{ownerId}/withdrawBalance").hasAuthority("SCOPE_accounts.client")
                .anyRequest().denyAll()
                .and()
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }
}