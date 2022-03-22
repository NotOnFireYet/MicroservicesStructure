package com.microsetvicetest.validate.controller;

import com.microsetvicetest.validate.domain.ResponseTemplate;
import com.microsetvicetest.validate.domain.User;
import com.microsetvicetest.validate.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginAPI {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginToAuth(User user){
        log.info("Redirecting {} to login", user.getUsername());
        try {
            return ResponseEntity.ok().body(authService.loginToAuth(user));
        } catch (Exception e){
            log.error("Login error: {}", e.getMessage());
            ResponseTemplate response = new ResponseTemplate("login error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
