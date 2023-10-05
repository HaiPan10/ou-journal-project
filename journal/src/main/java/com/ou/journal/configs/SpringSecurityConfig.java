package com.ou.journal.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.ou.journal.filter.CustomAccessDeniedHandler;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private CharacterEncodingFilter filter;

    // @Autowired
    // private CorsConfigurationSource corsConfigurationSource;

    // @Autowired
    // private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Bean
    // public JwtTokenFilter jwtTokenFilter() throws Exception {
    //     JwtTokenFilter jwtAuthenticationTokenFilter = new JwtTokenFilter();
    //     // jwtAuthenticationTokenFilter.setAuthenticationManager(authenticationManager(config));
    //     return jwtAuthenticationTokenFilter;
    // }

    // @Bean
    // public RestAuthenticationEntryPoint restServicesEntryPoint() {
    //     return new RestAuthenticationEntryPoint();
    // }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setPasswordEncoder(passwordEncoder);
        dao.setUserDetailsService(userDetailsService);
        return dao;
    }

    @Bean
    // @Order(2)
    public SecurityFilterChain getSpringSecurityChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider)
                .formLogin(login -> login.loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        // .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/?error"))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/resources/**",
                                "/css/**",
                                "/img/**",
                                "/js/**",
                                "/styles/**",
                                "/vendor/**",
                                "/pages/index",
                                "/login")
                        .permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(handling -> handling.accessDeniedHandler((requests, reponse, ex) -> {
                    System.out.printf("[EXCEPTION] - %s\n", ex.getMessage());
                    reponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                    reponse.getWriter().write("Forbidden!!!");
                }))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
