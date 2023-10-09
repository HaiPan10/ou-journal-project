package com.ou.journal.configs;

import java.util.Arrays;

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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.ou.journal.filter.ClientAuthenticationFilter;
import com.ou.journal.filter.ClientFailureHandler;
import com.ou.journal.filter.ClientSuccessHandler;
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
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired 
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CorsConfigurationSource corsConfiguration;

    @Autowired
    private ClientAuthenticationFilter clientAuthenticationFilter;

    @Bean
    public JwtTokenFilter jwtTokenFilter() throws Exception {
        JwtTokenFilter jwtAuthenticationTokenFilter = new JwtTokenFilter();
        return jwtAuthenticationTokenFilter;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new ClientSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new ClientFailureHandler();
    }

    @Bean
    public CustomAuthenticationEntryPoint restServicesEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new CompositeSessionAuthenticationStrategy(Arrays.asList(
            new ChangeSessionIdAuthenticationStrategy())
        );
    }

    @Bean
    public HttpSessionSecurityContextRepository httpContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setPasswordEncoder(passwordEncoder);
        dao.setUserDetailsService(userDetailsService);
        return dao;
    }

    @Bean
    public ClientAuthenticationFilter clientAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception{
        ClientAuthenticationFilter filter = new ClientAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        filter.setSecurityContextRepository(httpContextRepository());
        return filter;
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
                .cors(cors -> cors.configurationSource(corsConfiguration))
                .formLogin(login -> login.loginPage("/login").permitAll()
                        .usernameParameter("username")
                        .passwordParameter("password"))
                        // .successHandler(authenticationSuccessHandler)
                        // .failureHandler(authenticationFailureHandler))
                        // .defaultSuccessUrl("/submit", true)
                        // .failureUrl("/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/resources/**",
                                "/css/**",
                                "/img/**",
                                "/js/**",
                                "/styles/**",
                                "/vendor/**",
                                "/pages/index",
                                "/api/tests/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(handling -> handling.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(clientAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
