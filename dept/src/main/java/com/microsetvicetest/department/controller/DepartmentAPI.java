package com.microsetvicetest.department.controller;

import com.microsetvicetest.department.domain.User;
import com.microsetvicetest.department.service.DepartmentService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/department")
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentAPI {
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/login")
    public ResponseEntity<?> loginToAuth(User user){
        log.info("Redirecting {} to login", user.getUsername());
        try {
            return ResponseEntity.ok().body(departmentService.loginToAuth(user));
        } catch (Exception e){
            log.error("Login error: {}", e.getMessage());
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("error_type", e.getClass().getSimpleName());
            responseMap.put("error_message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @GetMapping("/show")
    public ResponseEntity<?> showDepartment(@RequestHeader("Authorization") String authHeader) {
        log.info("Fetching department from auth server");
        Map<String, String> responseMap = new HashMap<>();
        try {
            responseMap.put("department", departmentService.fetchDepartment(authHeader));
            return ResponseEntity.ok().body(responseMap);
        } catch (Exception e){
            log.error("Login error: {}", e.getMessage());
            responseMap.put("error_type", e.getClass().getSimpleName());
            responseMap.put("error_message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}
