package com.ou.journal.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.AccountStatus;
import com.ou.journal.enums.RoleName;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.AuthRequest;
import com.ou.journal.pojo.AuthResponse;
import com.ou.journal.pojo.CustomAuthenticationToken;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.AccountRepositoryJPA;
import com.ou.journal.repository.UserRepositoryJPA;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.CustomUserDetailsService;
import com.ou.journal.service.interfaces.UserRoleService;
import com.ou.journal.service.interfaces.UserService;

@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepositoryJPA accountRepositoryJPA;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserRepositoryJPA userRepositoryJPA;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    // đăng ký cho Author User
    public Account create(Account account, User user) throws Exception {
        try {
            if (accountRepositoryJPA.findByEmail(account.getEmail()).isPresent()) {
                throw new Exception(String.format("Email %s đã tồn tại!", account.getEmail()));
            }

            if (accountRepositoryJPA.findByUserName(account.getUserName()).isPresent()) {
                throw new Exception(String.format("Username %s đã tồn tại!", account.getUserName()));
            }
            userService.createAuthorUser(user);
            account.setUser(user);
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            account.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            // account.setVerificationCode(RandomStringUtils.randomAlphanumeric(64));
            // account.setStatus(AccountStatus.EMAIL_VERIFICATION.toString());
            // account.setStatus(AccountStatus.ACCEPTED.toString());
            accountRepositoryJPA.save(account);
            return account;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Account create(Account account, Long userId) throws Exception {
        User persistUser = null;
        try {
            persistUser = userService.retrieve(userId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        Optional<UserRole> userRole = userRoleService.getByUserAndRoleName(persistUser,
                RoleName.ROLE_AUTHOR.toString());
        if (!userRole.isPresent()) {
            userRoleService.addUserRole(persistUser, RoleName.ROLE_AUTHOR.toString());
        }
        account.setUser(persistUser);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        account.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        // account.setVerificationCode(RandomStringUtils.randomAlphanumeric(64));
        // account.setStatus(AccountStatus.EMAIL_VERIFICATION.toString());
        accountRepositoryJPA.save(account);
        return account;
    }

    @Override
    public boolean changeAccountStatus(Long accountId, String status) throws Exception {
        try {
            Account account = retrieve(accountId);
            // account.setStatus(status);
            accountRepositoryJPA.save(account);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Account> findAll() {
        return accountRepositoryJPA.findAll();
    }

    @Override
    public Account retrieve(Long accountId) throws Exception {
        Optional<Account> accountOptional = accountRepositoryJPA.findById(accountId);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        } else {
            throw new Exception("Tài khoản không tồn tại!");
        }
    }

    @Override
    public AuthResponse login(AuthRequest account) throws Exception {
        try {
            Optional<Account> accountOptional = accountRepositoryJPA.findByUserName(account.getUsername());
            if (!accountOptional.isPresent()) {
                throw new Exception("Email không tồn tại!");
            }
            // account.setUsername(String.format("%s,%s", account.getUsername(), account.getRole()));
            Authentication authentication = authenticationManager.authenticate(
                    new CustomAuthenticationToken(
                            account.getUsername(), account.getPassword(), account.getRole()));

            Account authenticationAccount = accountOptional.get();

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateAccessToken(authenticationAccount, account.getRole());

            AuthResponse authResponse = new AuthResponse();
            authResponse.setUser(authenticationAccount.getUser());
            authResponse.setAccessToken(token);
            return authResponse;
        } catch (AuthenticationException exception) {
            throw new Exception("Email hoặc mật khẩu không đúng.");
        }
    }

    @Override
    public SecurityContext login(String token) throws AccountNotFoundException, Exception {
        String roleName = jwtService.getRoleNameFromToken(token, SecrectType.EMAIL);
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new Exception("Invalid Role Name");
        }
        String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
        Optional<Account> accountOptional = accountRepositoryJPA.findByEmail(email);
        if (!accountOptional.isPresent()) {
            throw new AccountNotFoundException("Email không tồn tại");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsernameAndRoleName(email, roleName);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.getContextHolderStrategy().setContext(securityContext);
        return securityContext;
    }

    @Override
    public Account findByUserName(String userName) throws Exception {
        Optional<Account> accountOptional = accountRepositoryJPA.findByUserName(userName);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        } else {
            throw new Exception("Tài khoản không tồn tại!");
        }
    }

    @Override
    public boolean verifyEmail(Long accountId, String verificationCode) throws Exception {
        try {
            Account account = retrieve(accountId);
            // if (!account.getStatus().equals(AccountStatus.EMAIL_VERIFICATION.toString())) {
            //     throw new Exception("This account can't not be verified");
            // }
            // if (account.getVerificationCode().equals(verificationCode)) {
            //     account.setStatus(AccountStatus.PENDING.toString());
            //     accountRepositoryJPA.save(account);
            //     return true;
            // } else {
            //     throw new Exception("Verification code doesn't match!");
            // }
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Optional<Account> getAccount(Long accountId) {
        return accountRepositoryJPA.findById(accountId);
    }

    @Override
    public Optional<Account> getByEmail(String email) throws Exception {
        return accountRepositoryJPA.findByEmail(email);
    }

    @Override
    public void changeRole(Role role, User user) throws AccessDeniedException, Exception {
        try {
            UserRole userRole = userRoleService.findByUserAndRoleName(user, role.getRoleName());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getRoleName()));
            Authentication changedRoleAuth = new CustomAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(), authorities,
                    userRole.getRole().getRoleName());
            SecurityContextHolder.getContext().setAuthentication(changedRoleAuth);
        } catch (UsernameNotFoundException e) {
            throw new AccessDeniedException("User don't have specific role");
        } catch (Exception e) {
            throw new Exception("Bad request");
        }
    }
}
