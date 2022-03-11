package com.microservicetest.authserver.service;

import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.repo.IUserRepo;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    @TestSubject
    UserService userService;

    IUserRepo userRepo;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userRepo = createMock(IUserRepo.class);
        userService.setUserRepo(userRepo);
    }

    // Clears the security context to make sure the tests don't share it
    @After
    void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }

    // Checks the method in case of someone being logged in
    @Test
    void getActiveUserLoggedIn() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("username", null);
        SecurityContextHolder.getContext().setAuthentication(token);
        expect(userRepo.findByUsername("username")).andReturn(new AppUser());
        replay();

        assertDoesNotThrow(() -> userService.getActiveUser());
    }

    // Checks the method in case of nobody being logged in
    @Test
    void getActiveUserNoLogin() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertThrows(IllegalArgumentException.class,
            () -> userService.getActiveUser());
    }
}