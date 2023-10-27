package com.ou.journal.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/accounts")
public class ApiAccountController {
    // @Autowired
    // private JwtService jwtService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;
    
    @PostMapping("change-role")
    public ResponseEntity<?> changeRole(@RequestBody Role roleName, @AuthenticationPrincipal AuthenticationUser currentUser){
        try {
            Long id = currentUser.getId();
            User user = userService.retrieve(id);
            accountService.changeRole(roleName, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("create")
    public ResponseEntity<?> create(@RequestParam(required = false, name = "token") String token,
        HttpServletRequest httpServletRequest){
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(String.format("%s/register/account?token=%s", environment.getProperty("SERVER_HOSTNAME"), token)));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
