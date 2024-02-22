package com.banana.AccountsService.config;

import com.banana.AccountsService.exception.UserNotfoundException;
import com.banana.AccountsService.jwt.JwtTokenFilter;
import com.banana.AccountsService.model.ERole;
import com.banana.AccountsService.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Configuration
public class ApplicationSecurity {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationSecurity.class);

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                List<User> users = List.of(
                        new com.banana.AccountsService.model.User(1, "cliente1@example.com", passwordEncoder.encode("password1"), ERole.CLIENTE),
                        new com.banana.AccountsService.model.User(2, "cliente2@example.com", passwordEncoder.encode("password1"), ERole.CLIENTE),
                        new com.banana.AccountsService.model.User(3, "gestor3@example.com", passwordEncoder.encode("password1"), ERole.GESTOR)
                );
                return users.stream()
                        .filter(user -> user.getEmail().equals(email))
                        .findFirst()
                        .orElseThrow(UserNotfoundException::new);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        logger.info("Entra authenticationManager!!!!");
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // http.authenticationProvider(authProvider()); // can be commented

        http
                .authorizeHttpRequests((requests) -> requests
                        .antMatchers("/auth/login",
                                "/docs/**",
                                "/users",
                                "/h2-ui/**",
                                "/configuration/ui",
                                "/swagger-resources/**",
                                "/configuration/security",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        .antMatchers(HttpMethod.POST, "/accounts").hasAnyAuthority(ERole.GESTOR.name())
                        .antMatchers(HttpMethod.DELETE, "/accounts/owner/{ownerId}").hasAnyAuthority(ERole.GESTOR.name())
                        .antMatchers(HttpMethod.PUT, "/accounts/{id}/owner/{ownerId}/addBalance").hasAnyAuthority(ERole.CLIENTE.name())
                        .antMatchers(HttpMethod.PUT, "/accounts/{id}/owner/{ownerId}/withdrawBalance").hasAnyAuthority(ERole.CLIENTE.name())
                        .antMatchers("/**").denyAll()
                        .anyRequest().authenticated()
                );

        http.headers(headers ->
                headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin())
        );

        http.exceptionHandling((exception) -> exception.authenticationEntryPoint(
                (request, response, ex) -> {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            ex.getMessage()
                    );
                }
        ));

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}