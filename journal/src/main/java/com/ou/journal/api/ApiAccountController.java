package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.components.UserSessionInfo;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;

@RestController
@RequestMapping("api/accounts")
public class ApiAccountController {
    // @Autowired
    // private JwtService jwtService;

    @Autowired
    private UserSessionInfo userSessionInfo;

    @Autowired
    private AccountService accountService;
    
    @PostMapping("change-role")
    public ResponseEntity<?> changeRole(@RequestBody Role roleName){
        try {
            User user = userSessionInfo.getCurrentAccount().getUser();
            accountService.changeRole(roleName, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
