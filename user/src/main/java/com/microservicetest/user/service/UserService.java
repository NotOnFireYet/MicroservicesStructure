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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

@Service @Data
@AllArgsConstructor
@NoArgsConstructor @Slf4j
public class UserService {
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
        ResponseEntity<String> response = restTemplate.exchange(URL.concat("/login"), HttpMethod.POST, entity, String.class);

        if (userRepo.findByUsername(params.getUsername()) == null) { // Saves the user if they log in for the first time
            User user = new User();
            user.setUsername(params.getUsername());
            user.setPassword(params.getPassword());
            saveUser(user);
        }

        return response.getBody();
    }

    // Fetches the protected user info by passing an access token,
    // saves results to database
    public String fetchResource(String authHeader, String uri) {
        if (!authHeader.equals("")) {
            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(URL.concat(uri), HttpMethod.GET, entity, String.class);

                User user = userRepo.findByUsername(getActiveUsername(token));
                String resource = response.getBody();
                if (uri.equals("/user/department"))
                    user.setDepartment(resource);
                else
                    user.setOffice(resource);
                userRepo.save(user);

                return response.getBody();
            } else
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Authorization header must start with \"Bearer \" and contain a token");
        } else
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Null authorization header");
    }

    public User saveUser(User user) {
        log.info("Saving user {}", user.getUsername());
        return userRepo.save(user);
    }

    public User findUserById(Long UserId) {
        return userRepo.findByUserId(UserId);
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

    // Converts an object into a map that can be passed as request payload
    private MultiValueMap<String, String> paramsToValueMap(Object params){
        ObjectMapper objectMapper = new ObjectMapper();
        MultiValueMap valueMap = new LinkedMultiValueMap<String, String>();
        Map<String, Object> fieldMap = objectMapper.convertValue(params, new TypeReference<>() {});
        valueMap.setAll(fieldMap);
        return valueMap;
    }
}
