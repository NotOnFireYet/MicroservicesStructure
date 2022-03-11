package com.microservicetest.authserver.controller;

import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.service.TokenService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@AllArgsConstructor @Slf4j
@RequestMapping("/user")
public class UserAPI {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable("id") Long userId){
        return userService.findUserById(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
       return ResponseEntity.ok().body(userService.findAll());
   }

    @GetMapping("/department")
    public ResponseEntity<String> getDepartmentOfLoggedUser() {
        AppUser user = userService.getActiveUser();
        log.info("Fetching department for {}", user.getUsername());
        return ResponseEntity.ok().body(user.getDepartment());
    }

    @GetMapping("/office")
    public ResponseEntity<String> getOfficeOfLoggedUser(HttpServletResponse response) throws IOException {
        AppUser user = userService.getActiveUser();
        log.info("Fetching office for {}", user.getUsername());
        return ResponseEntity.ok().body(user.getOffice());
    }

    @GetMapping("/logged")
    public ResponseEntity<String> getActiveUsername(HttpServletResponse response) throws IOException {
        log.info("Fetching active user");
        return ResponseEntity.ok().body(userService.getActiveUser().getUsername());
    }
}
