package com.microservicetest.user.controller;

import com.microservicetest.user.domain.ParamsTemplate;
import com.microservicetest.user.domain.User;
import com.microservicetest.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserAPI {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String authorizeUser(ParamsTemplate params) {
        log.info("Redirecting to authorization");
        return userService.getTokensOnLogin(params);
    }

    @GetMapping("/validate")
    public String getDepartment(@RequestHeader("Authorization") String authHeader){
        log.info("Passing request to validate");
        return userService.checkAuthorized(authHeader);
    }

    @PostMapping("/save")
    public User saveUser(User User){
        log.info("Saving user {}", User.getUsername());
        return userService.saveUser(User);
    }

    @GetMapping("/all")
    public Collection<User> getAllUsers(){
        log.info("Fetching all users");
        return userService.getAllUsers();
    }
}
