package com.ou.journal.api;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.SecrectType;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/accounts")
public class ApiAccountController {
    @Autowired
    private JwtService jwtService;
    
    // Chưa hoàn thiện chỉ mới test lấy được account id
    // Chưa lưu lại reviewer đã xác nhận trong database trong bảng trung gian review_article
    // Chưa thêm role cho account được mời làm reviewer
    @GetMapping("reviewer/verify")
    public ResponseEntity<?> verifyReviewer(@RequestParam Map<String, String> params, HttpServletRequest httpServletRequest){
        Long id = jwtService.getIdFromToken(params.get("token"), SecrectType.EMAIL);
        System.out.println("[DEBUG] - ACCOUNT ID: " + id);
        return ResponseEntity.ok().body("Success");
    }
}
