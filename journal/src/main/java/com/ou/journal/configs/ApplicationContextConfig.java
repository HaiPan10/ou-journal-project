package com.ou.journal.configs;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ou.journal.pojo.Account;
import com.ou.journal.repository.AccountRepository;

@Configuration
@EnableTransactionManagement
public class ApplicationContextConfig implements WebMvcConfigurer{

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public UserDetailsService getUserDetail(){
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority(account.getUser().getUserRoles().get(0).getRole().getRoleName()));
                return new User(account.getEmail(), account.getPassword(), authorities);
            }
            
        };
    }

    @Bean
    public SimpleDateFormat getSimpleDate(){
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    @Bean(name="executorService")
    public ExecutorService getThreadPool() {
        // int threadNumber = Integer.parseInt(environment.getProperty("THREAD_NUMBER"));
        int threadNumber = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
        return executor;
    }
}
