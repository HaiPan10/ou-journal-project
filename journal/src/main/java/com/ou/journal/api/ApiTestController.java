package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.pojo.Account;
import com.ou.journal.service.interfaces.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/tests")
public class ApiTestController {
    @Autowired
    private AccountService accountService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> register (@Valid @RequestBody Account account, BindingResult bindingResult) throws Exception {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
