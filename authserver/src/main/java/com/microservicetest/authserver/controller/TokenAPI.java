package com.microservicetest.authserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.domain.TokenEntity;
import com.microservicetest.authserver.service.TokenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@AllArgsConstructor
@RequestMapping("/token") @Slf4j
public class TokenAPI {
    @Autowired
    private TokenService tokenService;

    // Returns a list of all tokens in the database
    @GetMapping("/all")
    public ResponseEntity<Collection<TokenEntity>> getAllTokenEntities(){
        return ResponseEntity.ok().body(tokenService.getAllTokens());
    }

    // Protected endpoint; if accessed, returns "authorized" to indicate
    // that access is granted
    @GetMapping("/validate")
    public ResponseEntity<String> validateAccess(HttpServletRequest request){
        return ResponseEntity.ok().body("authorized");
    }

    // Takes refresh token, sends back new access token
    @GetMapping("/refresh")
    public ResponseEntity<?> getNewAccessToken(HttpServletRequest request) {
        AppUser user = tokenService.validateToken(request);
        String accessToken = tokenService.generateAccessToken(user);

        // This block updates the tokens for user
        TokenEntity tokens = user.getTokenEntity();
        tokens.setAccessToken(accessToken);
        tokenService.saveTokens(tokens);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("access_token", accessToken);
        return ResponseEntity.ok().body(responseMap);
    }
}
