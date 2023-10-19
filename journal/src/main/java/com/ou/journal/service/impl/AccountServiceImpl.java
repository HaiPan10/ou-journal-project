package com.ou.journal.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.AccountStatus;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.AuthRequest;
import com.ou.journal.pojo.AuthResponse;
import com.ou.journal.repository.AccountRepositoryJPA;
import com.ou.journal.repository.UserRepositoryJPA;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
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

    @Override
    // đăng ký cho Author User
    public Account create(Account account) throws Exception {
        try {
            if (accountRepositoryJPA.findByEmail(account.getEmail()).isPresent()) {
                throw new Exception(String.format("Email %s đã tồn tại!", account.getEmail()));
            }
            userService.createAuthorUser(account.getUser());
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            account.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            account.setVerificationCode(RandomStringUtils.randomAlphanumeric(64));
            account.setStatus(AccountStatus.EMAIL_VERIFICATION.toString());
            accountRepositoryJPA.save(account);
            return account;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean changeAccountStatus(Long accountId, String status) throws Exception {
        try {
            Account account = retrieve(accountId);
            account.setStatus(status);
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

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            account.getUsername(), account.getPassword()));

            Account authenticationAccount = accountOptional.get();

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateAccessToken(authenticationAccount);

            // if (!authenticationAccount.getStatus().equals("ACTIVE")) {
            // // EXCEPTION JSON FOR CLIENT
            // String jsonString = new JSONObject()
            // .put("id", authenticationAccount.getId())
            // .put("status", authenticationAccount.getStatus())
            // .put("accessToken", token)
            // .toString();
            // throw new Exception(jsonString);
            // }

            if (authenticationAccount.getStatus().equals(AccountStatus.EMAIL_VERIFICATION.toString())) {
                throw new Exception("Chưa xác thực email!");
            } else if (authenticationAccount.getStatus().equals(AccountStatus.PENDING.toString())) {
                throw new Exception("Tài khoản đang chờ duyệt!");
            } else if (authenticationAccount.getStatus().equals(AccountStatus.REJECTED.toString())) {
                throw new Exception("Tài khoản không được duyệt!");
            } else {
                AuthResponse authResponse = new AuthResponse();
                authResponse.setUser(authenticationAccount.getUser());
                authResponse.setAccessToken(token);
                return authResponse;
            }
        } catch (AuthenticationException exception) {
            throw new Exception("Email hoặc mật khẩu không đúng.");
        }
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
            if (!account.getStatus().equals(AccountStatus.EMAIL_VERIFICATION.toString())) {
                throw new Exception("This account can't not be verified");
            }
            if (account.getVerificationCode().equals(verificationCode)) {
                account.setStatus(AccountStatus.PENDING.toString());
                accountRepositoryJPA.save(account);
                return true;
            } else {
                throw new Exception("Verification code doesn't match!");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
