package com.ou.journal.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.ou.journal.components.CustomAuthenticationProvider;
import com.ou.journal.components.CustomRememberServices;
import com.ou.journal.filter.ClientAuthenticationFilter;
import com.ou.journal.filter.AuthenticationFailureHandlerImpl;
import com.ou.journal.filter.AuthenticationSuccessHandlerImpl;
import com.ou.journal.filter.CustomAuthenticationEntryPoint;
import com.ou.journal.filter.JwtTokenFilter;
import com.ou.journal.service.interfaces.CustomUserDetailsService;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConfig {
    private final String REMEMBER_ME_SECRECT_KEY = "AFJAPIJFPIdfiohuipwehpwuy4e12y03478dcjkzncv.jadhr//.,l;k[qweru[o]]";
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CharacterEncodingFilter encodingFilter;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    @Qualifier("adminAuthenticationFailureHandler")
    private AuthenticationFailureHandler adminAuthenticationFailureHandler;
    @Autowired
    @Qualifier("adminAuthenticationSuccessHandler")
    private AuthenticationSuccessHandler adminAuthenticationSuccessHandler;
    @Autowired
    @Qualifier("corsConfigurationSource")
    private CorsConfigurationSource corsConfiguration;
    @Autowired
    private ClientAuthenticationFilter clientAuthenticationFilter;
    @Autowired
    @Qualifier("adminAuthenticationFilter")
    private ClientAuthenticationFilter adminAuthenticationFilter;
    @Autowired
    private RememberMeServices rememberMeServices;
    // @Autowired
    // private RememberMeAuthenticationFilter rememberMeAuthenticationFilter;

    @Bean
    public JwtTokenFilter jwtTokenFilter() throws Exception {
        JwtTokenFilter jwtAuthenticationTokenFilter = new JwtTokenFilter();
        return jwtAuthenticationTokenFilter;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl("/homepage");
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandlerImpl("/login?error");
    }

    @Bean("adminAuthenticationSuccessHandler")
    public AuthenticationSuccessHandler adminAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl("/admin/dashboard");
    }

    @Bean("adminAuthenticationFailureHandler")
    public AuthenticationFailureHandler adminAuthenticationFailureHandler() {
        return new AuthenticationFailureHandlerImpl("/admin/login?error");
    }

    @Bean
    public CustomAuthenticationEntryPoint restServicesEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new CompositeSessionAuthenticationStrategy(Arrays.asList(
                new ChangeSessionIdAuthenticationStrategy()));
    }

    @Bean
    public SecurityContextRepository httpContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(){
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public ClientAuthenticationFilter clientAuthenticationFilter(AuthenticationManager authenticationManager,
            RememberMeServices rememberMeServices)
            throws Exception {
        ClientAuthenticationFilter filter = new ClientAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        filter.setSecurityContextRepository(httpContextRepository());
        filter.setRememberMeServices(rememberMeServices);
        return filter;
    }

    @Bean("adminAuthenticationFilter")
    public ClientAuthenticationFilter adminAuthenticationFilter(AuthenticationManager authenticationManager,
            RememberMeServices rememberMeServices)
            throws Exception {
        ClientAuthenticationFilter filter = new ClientAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(adminAuthenticationFailureHandler());
        filter.setAuthenticationSuccessHandler(adminAuthenticationSuccessHandler());
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        filter.setSecurityContextRepository(httpContextRepository());
        filter.setRememberMeServices(rememberMeServices);
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/login", "POST"));
        return filter;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberServices services = new CustomRememberServices(REMEMBER_ME_SECRECT_KEY, userDetailsService);
        services.setTokenValiditySeconds(86400);
        return services;
    }

    // @Bean
    // public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
    //     RememberMeAuthenticationProvider provider = new RememberMeAuthenticationProvider(REMEMBER_ME_SECRECT_KEY);
    //     return provider;
    // }

    // @Bean
    // public RememberMeAuthenticationFilter rememberMeAuthenticationFilter(){
    //     RememberMeAuthenticationFilter filter = new RememberMeAuthenticationFilter(authenticationManager, rememberMeServices);
    //     return filter;
    // }

    @Bean
    public AuthenticationManager getAuthenticationManager(HttpSecurity httpSecurity,
            CustomAuthenticationProvider authenticationProvider)
            throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(authenticationProvider);
        // builder.authenticationProvider(rememberMeProvider);
        return builder.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .securityMatcher("/admin/**")
                .headers(headers -> headers.frameOptions(frame -> {
                    frame.sameOrigin();
                }))
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint))
                .rememberMe(rm -> rm.rememberMeServices(rememberMeServices))
                .formLogin(login -> login.loginPage("/admin/login").permitAll()
                        .usernameParameter("username")
                        .passwordParameter("password"))
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SECRETARY")
                        .anyRequest()
                        .authenticated())
                // .exceptionHandling(handling ->
                // handling.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(encodingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(adminAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .securityMatcher("/**")
                .headers(headers -> headers.frameOptions(frame -> {
                    frame.sameOrigin();
                }))
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint))
                .rememberMe(rm -> rm.rememberMeServices(rememberMeServices))
                .cors(cors -> cors.configurationSource(corsConfiguration))
                .formLogin(login -> login.loginPage("/login").permitAll()
                        .usernameParameter("username")
                        .passwordParameter("password"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/resources/**",
                                "/register",
                                "/register/email",
                                "/register/account",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/styles/**",
                                "/vendor/**",
                                "/pages/index",
                                "/api/tests/**",
                                "/api/review-articles/response",
                                "/reviewer-invite/create",
                                "/reviewer-invite/success",
                                "/api/accounts/reviewer/verify",
                                "/api/articles/author/article/withdraw",
                                "/api/accounts/create",
                                "/error-page",
                                "/api/accounts/login")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                // .exceptionHandling(handling ->
                // handling.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(encodingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(clientAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // .addFilterBefore(rememberMeAuthenticationFilter, ClientAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
