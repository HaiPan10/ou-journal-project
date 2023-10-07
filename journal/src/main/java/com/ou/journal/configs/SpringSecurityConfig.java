package com.ou.journal.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import com.ou.journal.filter.CustomAuthenticationEntryPoint;
import com.ou.journal.filter.JwtTokenFilter;

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

        @Autowired
        private CustomAccessDeniedHandler customAccessDeniedHandler;

        @Autowired
        private CustomAuthenticationEntryPoint authenticationEntryPoint;

<<<<<<< HEAD
        @Bean
        public PasswordEncoder getPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }
=======
//     @Autowired
//     private JwtTokenFilter jwtTokenFilter;

    @Bean
    public JwtTokenFilter jwtTokenFilter() throws Exception {
        JwtTokenFilter jwtAuthenticationTokenFilter = new JwtTokenFilter();
        // jwtAuthenticationTokenFilter.setAuthenticationManager(authenticationManager(config));
        return jwtAuthenticationTokenFilter;
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
>>>>>>> 961894e881d994c832e770e06af75aba72d6c55a

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public CustomAccessDeniedHandler customAccessDeniedHandler() {
                return new CustomAccessDeniedHandler();
        }

        @Bean
        public CustomAuthenticationEntryPoint restServicesEntryPoint() {
                return new CustomAuthenticationEntryPoint();
        }

        @Bean
        public AuthenticationProvider getAuthenticationProvider() {
                DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
                dao.setPasswordEncoder(passwordEncoder);
                dao.setUserDetailsService(userDetailsService);
                return dao;
        }

        @Bean
        @Order(1)
        public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .securityMatcher("/admin/**")
                                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint))
                                .authenticationProvider(authenticationProvider)
                                .formLogin(login -> login.loginPage("/admin/login").permitAll()
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/admin/dashboard", true)
                                                .failureUrl("/admin/login?error"))
                                .logout(logout -> logout
                                                .logoutUrl("/admin/logout")
                                                .logoutSuccessUrl("/admin/login")
                                                .deleteCookies("JSESSIONID")
                                                .invalidateHttpSession(true))
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                                .anyRequest()
                                                .authenticated())
                                .exceptionHandling(handling -> handling.accessDeniedHandler(customAccessDeniedHandler))
                                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

    @Bean
    @Order(2)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .securityMatcher("/**")
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint))
                .authenticationProvider(authenticationProvider)
                .formLogin(login -> login.loginPage("/login").permitAll()
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/submit", true)
                        .failureUrl("/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                                                        .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                // .authorizeHttpRequests(requests -> requests
                //         .requestMatchers(
                //                 "/resources/**",
                //                 "/css/**",
                //                 "/img/**",
                //                 "/js/**",
                //                 "/styles/**",
                //                 "/vendor/**",
                //                 "/pages/index",
                //                 "/api/tests/**")
                //         .permitAll()
                //         .anyRequest()
                //         .authenticated())
                // .exceptionHandling(handling -> handling.accessDeniedHandler(customAccessDeniedHandler))
                // .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                // .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
