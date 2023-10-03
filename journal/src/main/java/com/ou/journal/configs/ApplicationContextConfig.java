// package com.ou.journal.configs;

<<<<<<< HEAD
// import java.text.SimpleDateFormat;
// import java.util.HashSet;
// import java.util.Set;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.transaction.annotation.EnableTransactionManagement;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import com.ou.journal.pojo.Account;
// import com.ou.journal.repository.AccountRepository;

// @Configuration
// @EnableTransactionManagement
// public class ApplicationContextConfig implements WebMvcConfigurer{
=======
import java.text.SimpleDateFormat;
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
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ou.journal.components.DateFormatter;
import com.ou.journal.pojo.Account;
import com.ou.journal.repository.AccountRepository;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:configs.properties")
public class ApplicationContextConfig implements WebMvcConfigurer{
>>>>>>> 4a5d4579f081aba68574ef8f69374f3fa116ce71

//     @Autowired
//     private AccountRepository accountRepository;

<<<<<<< HEAD
//     @Bean
//     public UserDetailsService getUserDetail(){
//         return new UserDetailsService() {
=======
    @Autowired
    private Environment environment;

    @Autowired
    private DateFormatter dateFormatter;

    @Bean
    public UserDetailsService getUserDetail(){
        return new UserDetailsService() {
>>>>>>> 4a5d4579f081aba68574ef8f69374f3fa116ce71

//             @Override
//             public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//                 Account account = accountRepository.findByEmail(email)
//                     .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                
//                 Set<GrantedAuthority> authorities = new HashSet<>();
//                 authorities.add(new SimpleGrantedAuthority(account.getUser().getUserRoles().get(0).getRole().getRoleName()));
//                 return new User(account.getEmail(), account.getPassword(), authorities);
//             }
            
//         };
//     }

<<<<<<< HEAD
//     @Bean
//     public SimpleDateFormat getSimpleDate(){
//         return new SimpleDateFormat("yyyy-MM-dd");
//     }

//     @Bean(name="executorService")
//     public ExecutorService getThreadPool() {
//         // int threadNumber = Integer.parseInt(environment.getProperty("THREAD_NUMBER"));
//         int threadNumber = 10;
//         ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
//         return executor;
//     }
// }
=======
    @Bean
    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", environment.getProperty("CLOUDINARY_CLOUD_NAME"),
                "api_key", environment.getProperty("CLOUDINARY_API_KEY"),
                "api_secret", environment.getProperty("CLOUDINARY_API_SECRET"),
                "secure", true));
    }

    @Bean
    public SimpleDateFormat getSimpleDate(){
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

    @Bean(name="executorService")
    public ExecutorService getThreadPool() {
        // int threadNumber = Integer.parseInt(environment.getProperty("THREAD_NUMBER"));
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
>>>>>>> 4a5d4579f081aba68574ef8f69374f3fa116ce71
