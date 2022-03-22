package com.microservicetest.authserver.service;

import com.microservicetest.authserver.repo.IUserRepo;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.easymock.EasyMock.createMock;

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
}