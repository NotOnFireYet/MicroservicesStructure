package com.microsetvicetest.department.service;

import com.microsetvicetest.department.config.AppConfig;
import com.microsetvicetest.department.domain.User;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepartmentServiceTest {
    @TestSubject
    DepartmentService departmentService;

    AppConfig config;
    RestTemplate restTemplate;
    String URL = "http://localhost:9002/user/";

    @BeforeEach
    void setUp(){
        restTemplate = createMock(RestTemplate.class);
        config = createMock(AppConfig.class);
        departmentService = new DepartmentService(config, restTemplate, URL);
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
                eq(URL.concat("/department")), eq(HttpMethod.GET), anyObject(HttpEntity.class), eq(String.class)))
            .andReturn(responseEntity).once();
        replay(restTemplate);

        assertEquals("body", departmentService.fetchDepartment("Bearer ".concat(token)));
    }

    // Checks if fetching fails with null header
    @Test
    void fetchResourceNullHeader() {
        assertThrows(HttpClientErrorException.class,
            () -> departmentService.fetchDepartment(""));
    }

    // Checks if fetching fails with incomplete header
    @Test
    void fetchResourceIncompleteHeader() {
        assertThrows(Exception.class,
            () -> departmentService.fetchDepartment("Bearer "));
    }

    // Checks if fetching fails with non-bearer access token
    @Test
    void fetchResourceNonBearerHeader() {
        assertThrows(Exception.class,
            () -> departmentService.fetchDepartment("111111"));
    }
}