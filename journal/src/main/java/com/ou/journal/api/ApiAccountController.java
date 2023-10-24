package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.configs.JwtService;

@RestController
@RequestMapping("api/accounts")
public class ApiAccountController {
    @Autowired
    private JwtService jwtService;
    
    
}
