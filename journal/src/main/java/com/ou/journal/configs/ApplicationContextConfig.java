package com.ou.journal.configs;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ou.journal.components.DateFormatter;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.AccountRepositoryJPA;
import com.ou.journal.service.interfaces.UserRoleService;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:configs.properties")
public class ApplicationContextConfig implements WebMvcConfigurer {

    @Autowired
    private AccountRepositoryJPA accountRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private DateFormatter dateFormatter;

    @Autowired
    private UserRoleService userRoleService;

    @Bean("getUserDetail")
    public UserDetailsService getUserDetail() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
                String[] split = StringUtils.split(userName, ",");
                String roleName = "";
                if (split != null && split.length == 2) {
                    
                    userName = split[0];
                    roleName = split[1];
                    System.out.println("[DEBUG] - Details: " + userName + " " + roleName);
                }
                Account account = accountRepository.findByUserName(userName)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                Set<GrantedAuthority> authorities = new HashSet<>();
                if (!roleName.isEmpty()) {
                    UserRole userRole = userRoleService.findByUserAndRoleName(account.getUser(), roleName);
                    authorities.add(new SimpleGrantedAuthority(userRole.getRole().getRoleName()));
                } else {
                    Set<UserRole> userRoles = account.getUser().getUserRoles();
                    userRoles.forEach(ur -> {
                        authorities.add(new SimpleGrantedAuthority(ur.getRole().getRoleName()));
                    });
                }

                return new User(account.getUserName(), account.getPassword(), authorities);
            }

        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // String clientHostname = environment.getProperty("CLIENT_HOSTNAME");
        CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigins(Collections.singletonList(clientHostname));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token"));
        // configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // configuration.setAllowCredentials(true);
        // configuration.setExposedHeaders(Arrays.asList("X-Auth-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", environment.getProperty("CLOUDINARY_CLOUD_NAME"),
                "api_key", environment.getProperty("CLOUDINARY_API_KEY"),
                "api_secret", environment.getProperty("CLOUDINARY_API_SECRET"),
                "secure", true));
    }

    @Bean
    public SimpleDateFormat getSimpleDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "executorService")
    public ExecutorService getThreadPool() {
        // int threadNumber =
        // Integer.parseInt(environment.getProperty("THREAD_NUMBER"));
        int threadNumber = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
        return executor;
    }

    @Bean(name = "scheduledExecutorService")
    public ScheduledExecutorService getScheduledService() {
        int threadNumber = Integer.parseInt(environment.getProperty("THREAD_NUMBER"));
        ScheduledExecutorService configs = Executors.newScheduledThreadPool(threadNumber);
        return configs;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // registry.addFormatter(new CategoryFormatter());
        // In case of needed to format fields of pojo
        // create new class and
        // implements the Formatter<T> interface
        // might not necessary
        registry.addFormatter(dateFormatter);
    }

    @Bean(name = "validator")
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Override
    public Validator getValidator() {
        return validator();
    }
}
