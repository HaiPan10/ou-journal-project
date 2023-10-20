package com.ou.journal.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
    public SecurityFilterChain getSpringSecurityChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider)
                .formLogin(login -> login.loginPage("/")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        // .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/?error"))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                // mvc.pattern("/resources/**"),
                                // mvc.pattern("/css/**"),
                                // mvc.pattern("/img/**"),
                                // mvc.pattern("/js/**"),
                                // mvc.pattern("/styles/**"),
                                // mvc.pattern("/vendor/**"),
                                // mvc.pattern("/pages/index"),
                                "/")
                        .permitAll()
                        // .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(handling -> handling.accessDeniedHandler((requests, reponse, ex) -> {
                    System.out.printf("[EXCEPTION] - %s\n", ex.getMessage());
                    reponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                    reponse.getWriter().write("Forbidden!!!");
                }));
        return http.build();
    }
}
