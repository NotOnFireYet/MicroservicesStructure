package com.microservicetest.user.service;

import com.microservicetest.user.config.AppConfig;
import com.microservicetest.user.domain.ParamsTemplate;
import com.microservicetest.user.domain.User;
import com.microservicetest.user.repo.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserServiceTest {
    @TestSubject
    UserService userService;

    IUserRepo userRepo;
    RestTemplate restTemplate;
    AppConfig config;

    String URL = "http://localhost:9004";

    @BeforeEach
    void setUp(){
        restTemplate = createMock(RestTemplate.class);
        config = createMock(AppConfig.class);
        userRepo = createMock(IUserRepo.class);
        userService = new UserService(config, userRepo, restTemplate, URL);
    }

    // Checks if the method receives a successfull response from auth server
    @Test
    void getTokensOnLogin() {
        ParamsTemplate params = new ParamsTemplate("username", "password", 1L, "client_secret");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("body", new HttpHeaders(), HttpStatus.OK);
        expect(restTemplate
            .exchange(
                eq(URL.concat("/login")), eq(HttpMethod.POST), anyObject(HttpEntity.class), eq(String.class)))
            .andReturn(responseEntity);
        expect(userRepo.findByUsername(params.getUsername())).andReturn(new User());
        replay(restTemplate, userRepo);

        assertNotNull(userService.getTokensOnLogin(params));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    // Checks if fetching works with valid header
    @Test
    void fetchResourceValidHeader() {
        String token = "valid_token";
        String uri = "/user/department";
        User user = new User();
        user.setUsername("username");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("body", new HttpHeaders(), HttpStatus.OK);
        expect(restTemplate
            .exchange(
                eq(URL.concat(uri)), eq(HttpMethod.GET), anyObject(HttpEntity.class), eq(String.class)))
            .andReturn(responseEntity).once();

        ResponseEntity<String> responseUser = new ResponseEntity<>(user.getUsername(), new HttpHeaders(), HttpStatus.OK);
        expect(restTemplate
            .exchange(
                eq(URL.concat("/user/logged")), eq(HttpMethod.GET), anyObject(HttpEntity.class), eq(String.class)))
            .andReturn(responseUser).once();

        expect(userRepo.findByUsername(user.getUsername())).andReturn(user);
        expect(userRepo.save(user)).andReturn(user);
        replay(restTemplate, userRepo);

        assertEquals("body", userService.fetchResource("Bearer ".concat(token), uri));
    }

    // Checks if fetching fails with null header
    @Test
    void fetchResourceNullHeader() {
        assertThrows(HttpClientErrorException.class,
            () -> userService.fetchResource("", "/uri"));
    }

    // Checks if fetching fails with incomplete header
    @Test
    void fetchResourceIncompleteHeader() {
        assertThrows(Exception.class,
            () -> userService.fetchResource("Bearer 1111111", "/uri"));
    }

    // Checks if fetching fails with non-bearer access token
    @Test
    void fetchResourceNonBearerHeader() {
        assertThrows(Exception.class,
            () -> userService.fetchResource("1111111", "/uri"));
    }
}