package com.ou.journal.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService extends UserDetailsService{
    UserDetails loadUserByUsernameAndRoleName(String username, String roleName) throws UsernameNotFoundException, Exception;
}
