package com.microservicetest.authserver.controller;

import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@AllArgsConstructor @Slf4j
@RequestMapping("/user")
public class UserAPI {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable("id") Long userId){
        return userService.findUserById(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
       return ResponseEntity.ok().body(userService.findAll());
   }
}
