package com.microservicetest.authserver.filter;

import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.service.TokenService;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import java.io.IOException;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomAuthorizationFilterTest {
    @TestSubject
    CustomAuthorizationFilter filter;

    TokenService tokenService;

    FilterChain chain;

    @BeforeEach
    void setUp(){
        tokenService = createMock(TokenService.class);
        filter = new CustomAuthorizationFilter(tokenService);
    }

    // Checks if authorization goes through with valid token
    @Test
    void doFilterInternalValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        expect(tokenService.validateToken(request)).andReturn(new AppUser());
        replay(tokenService);

        chain = createMock(FilterChain.class);
        filter.doFilterInternal(request, response, chain);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // Checks if authorization fails with invalid token
    @Test
    void doFilterInternalInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        expect(tokenService.validateToken(request)).andReturn(null);
        replay(tokenService);

        chain = createMock(FilterChain.class);
        filter.doFilterInternal(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}