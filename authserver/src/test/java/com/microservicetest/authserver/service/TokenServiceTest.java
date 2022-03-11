package com.microservicetest.authserver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.microservicetest.authserver.config.SecurityConfig;
import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.domain.TokenEntity;
import com.microservicetest.authserver.repo.ITokenRepo;

import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenServiceTest {

    @TestSubject
    TokenService tokenService;

    ITokenRepo tokenRepo;

    UserService userService;

    SecurityConfig config;

    static Algorithm algorithm = Algorithm.HMAC256(("secret").getBytes());

    @BeforeEach
    void setUp(){
        userService = createMock(UserService.class);
        tokenRepo = createMock(ITokenRepo.class);
        config = createMock(SecurityConfig.class);

        tokenService = new TokenService();
        tokenService.setAlgorithm(algorithm);
        tokenService.setTokenRepo(tokenRepo);
        tokenService.setUserService(userService);
        tokenService.setConfig(config);
    }

    // Checks that access token is generated correctly and can be decoded
    @Test
    void generateAccessToken() {
        AppUser user = new AppUser();
        user.setUsername("username");
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(tokenService.generateAccessToken(user));
        String decodedSubject = decodedToken.getSubject();

        assertEquals(decodedSubject, user.getUsername());
    }

    // Checks that access token contains the correct type
    @Test
    void generateAccessTokenHasType() {
        AppUser user = new AppUser();
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(tokenService.generateAccessToken(user));
        Claim decodedClaim = decodedToken.getClaim("type");

        assertEquals("access", decodedClaim.asString());
    }

    // Checks that refresh token is generated correctly and can be decoded
    @Test
    void generateRefreshToken() {
        AppUser user = new AppUser();
        user.setUsername("username");
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(tokenService.generateRefreshToken(user));
        String decodedSubject = decodedToken.getSubject();

        assertEquals(decodedSubject, user.getUsername());
    }

    // Checks that refresh token contains the correct type
    @Test
    void generateRefreshTokenHasType() {
        JWTVerifier verifier = JWT.require(algorithm).build();
        AppUser user = new AppUser();
        DecodedJWT decodedToken = verifier.verify(tokenService.generateRefreshToken(user));
        Claim decodedClaim = decodedToken.getClaim("type");

        assertEquals("refresh", decodedClaim.asString());
    }

    // Checks if valid token passes validation
    @Test
    void validateTokenCorrectToken() {
        AppUser user = new AppUser();
        user.setUsername("username");
        TokenEntity tokens = new TokenEntity(null, user, tokenService.generateAccessToken(user), tokenService.generateRefreshToken(user));
        user.setTokenEntity(tokens);
        expect(userService.findByUsername(user.getUsername())).andReturn(user);
        replay(userService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer ".concat(tokens.getAccessToken()));

        assertNotNull(tokenService.validateToken(request));
    }

    // Checks if invalid token fails validation
    @Test
    void validateTokenWrongToken() {
        AppUser user = new AppUser();
        user.setUsername("username");
        TokenEntity tokens = new TokenEntity(null, user, tokenService.generateAccessToken(user), tokenService.generateRefreshToken(user));
        user.setTokenEntity(tokens);
        expect(userService.findByUsername(user.getUsername())).andReturn(user);
        replay(userService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "invalid_token");

        assertThrows(IllegalArgumentException.class,
            () -> tokenService.validateToken(request));
    }

    // Chekcs if correct client credentials pass validation
    @Test
    void validateClientCorrectCredentials() {
        expect(config.getClientId()).andReturn(1L);
        expect(config.getClientSecret()).andReturn("secret");
        replay(config);

        assertDoesNotThrow(() -> tokenService.validateClient(1L, "secret"));
    }

    // Chekcs if invalid client credentials pass validation
    @Test
    void validateClientWrongCredentials() {
        expect(config.getClientId()).andReturn(1L);
        expect(config.getClientSecret()).andReturn("secret");
        replay(config);

        assertThrows(HttpClientErrorException.class,
            () -> tokenService.validateClient(2L, "s3cr3t"));
    }
}