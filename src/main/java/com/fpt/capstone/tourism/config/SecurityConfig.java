package com.fpt.capstone.tourism.config;

import com.fpt.capstone.tourism.helper.OAuth2AuthenticationSuccessHandler;
import com.fpt.capstone.tourism.service.impl.user.CustomOAuth2UserService;
import com.fpt.capstone.tourism.service.impl.user.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JWTAuthFilter jwtAuthFilter;
    private final OAuth2AuthenticationSuccessHandler successHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,  CustomOAuth2UserService oauth2UserService) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/swagger-ui/**", "/v1/v3/api-docs/**","/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers("/public/**", "/auth/**", "/oauth2/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/forgot-password", "/reset-password").permitAll()
                        .requestMatchers("/v1/admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/v1/marketing/**").hasAnyAuthority("MARKETING_MANAGER", "BUSINESS_DEPARTMENT", "ADMIN")
                        .requestMatchers("/v1/salesman/**").hasAnyAuthority("SELLER", "BUSINESS_DEPARTMENT", "ADMIN")
                        .requestMatchers("/v1/operator/**").hasAnyAuthority("SERVICE_COORDINATOR", "BUSINESS_DEPARTMENT", "ADMIN")
                        .requestMatchers("/v1/accountant/**").hasAnyAuthority("ACCOUNTANT", "BUSINESS_DEPARTMENT", "ADMIN")
                        .requestMatchers("/v1/service-provider/**").hasAnyAuthority("BUSINESS_DEPARTMENT", "ADMIN")
                        .requestMatchers("/v1/head-of-business/**").hasAnyAuthority("BUSINESS_DEPARTMENT", "ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                )
                .rememberMe(rememberMe ->
                        rememberMe.key("tempKey")
                                .rememberMeCookieName("remember-me")
                                .tokenValiditySeconds(7 * 24 * 60 * 60)
                                .rememberMeParameter("remember-me")
                                .useSecureCookie(false)
                                .userDetailsService(userDetailsService)
                );
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
