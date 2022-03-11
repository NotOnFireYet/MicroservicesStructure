package com.microservicetest.office.controller;

import com.microservicetest.office.domain.User;
import com.microservicetest.office.service.OfficeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/office")
public class OfficeAPI {
    @Autowired
    private OfficeService officeService;

    @PostMapping("/login")
    public ResponseEntity<?> loginToAuth(User user){
        log.info("Redirecting {} to login", user.getUsername());
        try {
            return ResponseEntity.ok().body(officeService.loginToAuth(user));
        } catch (Exception e){
            log.error("Login error: {}", e.getMessage());
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("error_type", e.getClass().getSimpleName());
            responseMap.put("error_message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @GetMapping("/show")
    public ResponseEntity<?> showOffice(@RequestHeader("Authorization") String authHeader) {
        log.info("Fetching office from auth server");
        Map<String, String> responseMap = new HashMap<>();
        try {
            responseMap.put("department", officeService.fetchOffice(authHeader));
            return ResponseEntity.ok().body(responseMap);
        } catch (Exception e){
            log.error("Login error: {}", e.getMessage());
            responseMap.put("error_type", e.getClass().getSimpleName());
            responseMap.put("error_message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

}
