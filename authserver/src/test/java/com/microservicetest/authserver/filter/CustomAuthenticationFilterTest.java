package com.microservicetest.authserver.filter;

import com.microservicetest.authserver.service.TokenService;
import com.microservicetest.authserver.service.UserService;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomAuthenticationFilterTest {
    @TestSubject
    CustomAuthenticationFilter filter;
    AuthenticationManager authenticationManager;
    TokenService tokenService;
    UserService userService;

    static String clientSecret = "secret";
    static Long clientId = 1L;
    @BeforeEach
    void setUp() {
        authenticationManager = createMock(AuthenticationManager.class);
        tokenService = createMock(TokenService.class);
        userService = createMock(UserService.class);
        filter = new CustomAuthenticationFilter(authenticationManager, tokenService, userService);
    }

    // Checks method with all valid credentials
    @Test
    void attemptAuthenticationCorrectCredentials() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addParameter("username", "username");
        request.addParameter("password", "1111");
        request.addParameter("client_secret", clientSecret);
        request.addParameter("client_id", clientId.toString());

        tokenService.validateClient(clientId, clientSecret);
        expectLastCall();
        replay(tokenService);

        assertDoesNotThrow(() -> filter.attemptAuthentication(request, response));
    }

    // Checks if method fails with missing credentials
    @Test
    void attemptAuthenticationMissingCredentials() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addParameter("username", "username");
        request.addParameter("password", "1111");

        assertThrows(Exception.class,
            () -> filter.attemptAuthentication(request, response));
    }
}