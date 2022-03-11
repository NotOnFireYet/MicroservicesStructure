package com.microservicetest.office.service;

import com.microservicetest.office.config.AppConfig;
import com.microservicetest.office.domain.User;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfficeServiceTest {
    @TestSubject
    OfficeService departmentService;

    AppConfig config;
    RestTemplate restTemplate;
    String URL = "http://localhost:9002/user/";

    @BeforeEach
    void setUp(){
        restTemplate = createMock(RestTemplate.class);
        config = createMock(AppConfig.class);
        departmentService = new OfficeService(config, restTemplate, URL);
    }

    // Checks if the login request works correctly
    @Test
    void loginToAuth() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("body", new HttpHeaders(), HttpStatus.OK);

        expect(config.getClientId()).andReturn(1L);
        expect(config.getClientSecret()).andReturn("secret");
        expect(restTemplate
            .exchange(
                eq(URL.concat("/login")), eq(HttpMethod.POST), anyObject(HttpEntity.class), eq(String.class)))
            .andReturn(responseEntity).once();
        replay(config, restTemplate);

        assertEquals("body", departmentService.loginToAuth(new User()));
    }

    // Checks if the department fetches with correct token
    @Test
    void fetchDepartment() {
        String token = "valid_token";
        User user = new User();
        user.setUsername("username");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("body", new HttpHeaders(), HttpStatus.OK);
        expect(restTemplate
            .exchange(
                eq(URL.concat("/office")), eq(HttpMethod.GET), anyObject(HttpEntity.class), eq(String.class)))
            .andReturn(responseEntity).once();
        replay(restTemplate);

        assertEquals("body", departmentService.fetchOffice("Bearer ".concat(token)));
    }

    // Checks if fetching fails with null header
    @Test
    void fetchResourceNullHeader() {
        assertThrows(HttpClientErrorException.class,
            () -> departmentService.fetchOffice(""));
    }

    // Checks if fetching fails with incomplete header
    @Test
    void fetchResourceIncompleteHeader() {
        assertThrows(Exception.class,
            () -> departmentService.fetchOffice("Bearer "));
    }

    // Checks if fetching fails with non-bearer access token
    @Test
    void fetchResourceNonBearerHeader() {
        assertThrows(Exception.class,
            () -> departmentService.fetchOffice("111111"));
    }
}