package com.ou.journal.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.AccountRepositoryJPA;
import com.ou.journal.service.interfaces.CustomUserDetailsService;
import com.ou.journal.service.interfaces.UserRoleService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    @Autowired
    private AccountRepositoryJPA accountRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = retrieveAccount(username);
        Set<GrantedAuthority> authorities = new HashSet<>();
        account.getUser().getUserRoles().forEach(ur -> {
            authorities.add(new SimpleGrantedAuthority(ur.getRole().getRoleName()));
        });

        return createAuthenticationUser(account, null, authorities);
    }

    @Override
    public UserDetails loadUserByUsernameAndRoleName(String username, String roleName)
            throws UsernameNotFoundException, Exception {
        Account account = retrieveAccount(username);

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (roleName != null && !roleName.isEmpty() && !(roleName.equals(RoleName.ROLE_ADMIN.toString())
                || roleName.equals(RoleName.ROLE_SECRETARY.toString()))) {
            UserRole userRole = userRoleService.findByUserAndRoleName(account.getUser(), roleName);
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getRoleName()));
            if (roleName.equals(RoleName.ROLE_EDITOR.toString())) {
                try {
                    UserRole userChiefEditorRole = userRoleService.findByUserAndRoleName(account.getUser(),
                            RoleName.ROLE_CHIEF_EDITOR.toString());
                    authorities.add(new SimpleGrantedAuthority(userChiefEditorRole.getRole().getRoleName()));
                } catch (Exception e) {
                    System.out.println("[DEBUG] - Normal Editor");
                }
            }
        } else {
            // Set<UserRole> userRoles = account.getUser().getUserRoles();
            // userRoles.forEach(ur -> {
            // authorities.add(new SimpleGrantedAuthority(ur.getRole().getRoleName()));
            // });
            UserRole userRole = null;
            try {
                userRole = userRoleService.findByUserAndRoleName(account.getUser(), RoleName.ROLE_ADMIN.toString());
                roleName = RoleName.ROLE_ADMIN.toString();
            } catch (UsernameNotFoundException e) {
                userRole = userRoleService.findByUserAndRoleName(account.getUser(), RoleName.ROLE_SECRETARY.toString());
                roleName = RoleName.ROLE_SECRETARY.toString();
            }
            if (userRole == null) {
                throw new UsernameNotFoundException("User not found");
            }
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getRoleName()));
        }

        return createAuthenticationUser(account, roleName, authorities);
    }

    private Account retrieveAccount(String username) {
        Account account = null;
        if (username.contains("@")) {
            account = accountRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            account = accountRepository.findByUserName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        return account;
    }

    private AuthenticationUser createAuthenticationUser(Account account, String roleName,
            Set<GrantedAuthority> authorities) {
        AuthenticationUser authenticationUser = new AuthenticationUser(account.getUserName(),
                account.getPassword(), authorities);
        authenticationUser.setId(account.getId());
        authenticationUser.setFirstName(account.getUser().getFirstName());
        authenticationUser.setLastName(account.getUser().getLastName());
        authenticationUser.setEmail(account.getEmail());
        if (roleName != null) {
            authenticationUser.setRoleName(roleName);
        }
        return authenticationUser;
    }

}
