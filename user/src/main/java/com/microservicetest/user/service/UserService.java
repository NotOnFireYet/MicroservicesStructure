package com.microservicetest.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicetest.user.config.AppConfig;
import com.microservicetest.user.domain.ParamsTemplate;
import com.microservicetest.user.domain.User;
import com.microservicetest.user.repo.IUserRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

@Service @Data
@AllArgsConstructor
@NoArgsConstructor @Slf4j
public class UserService implements IUserService {
    @Autowired
    private AppConfig config;

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private RestTemplate restTemplate;

    private String URL;

    @PostConstruct
    public void postConstruct(){
        URL = config.getAuthURL();
    }

    // Gets the user info and client info from API request,
    // passes it to auth server for authentication
    public String getTokensOnLogin(ParamsTemplate params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(paramsToValueMap(params), headers);
        ResponseEntity<String> response = restTemplate.exchange(URL + "/login", HttpMethod.POST, entity, String.class);

        if (userRepo.findByUsername(params.getUsername()) == null) { // Saves the user if they log in for the first time
            User user = new User();
            user.setUsername(params.getUsername());
            user.setPassword(params.getPassword());
            saveUser(user);
        }

        return response.getBody();
    }

    // Forwards the check if the request is authorized to auth server
    public String checkAuthorized(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(URL + "/token/validate", HttpMethod.GET, entity, String.class).getBody();
    }

    public User saveUser(User user) {
        log.info("Saving user {}", user.getUsername());
        return userRepo.save(user);
    }

    public Collection<User> getAllUsers() {
        return userRepo.findAll();
    }

    // Gets the username of the active user from the auth server,
    // saves to database
    public String getActiveUsername(String authToken){
        log.info("Fetching active user");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(URL.concat("/user/logged"), HttpMethod.GET, entity, String.class).getBody();
    }
}
