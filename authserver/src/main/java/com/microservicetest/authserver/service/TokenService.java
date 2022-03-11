package com.microservicetest.authserver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.microservicetest.authserver.config.SecurityConfig;
import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.domain.TokenEntity;
import com.microservicetest.authserver.repo.ITokenRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;

@Data
@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TokenService {
    @Autowired
    private ITokenRepo tokenRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConfig config;

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(config.getClientSecret().getBytes());
    }

    public String generateAccessToken(AppUser user){
        return JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 5*60*1000))
            .withClaim("username", user.getUsername())
            .withClaim("type", "access")
            .sign(algorithm);
    }

    public String generateRefreshToken(AppUser user) {
        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("type", "refresh")
            .withExpiresAt(new Date(System.currentTimeMillis() + 24*60*60*1000))
            .sign(algorithm);
    }

    public AppUser validateToken(HttpServletRequest request) throws RuntimeException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null) {
            if (authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length()); // Gets token from header
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedToken = verifier.verify(token);

                AppUser user = userService.findByUsername(decodedToken.getSubject());
                TokenEntity tokens = user.getTokenEntity();

                if (tokens.getRefreshToken() != null) { // Checking if the user has ever logged in
                    String tokenType = decodedToken.getClaim("type").asString();
                    String existingToken = (tokenType.equals("access")) ?     // Checks which type of token it's verifying
                        tokens.getAccessToken() : tokens.getRefreshToken();   // and gets the one that's in the database
                    if (token.equals(existingToken)) {
                        log.info("Validated token for {}", user.getUsername());
                        return user;
                    }
                    else
                        throw new IllegalArgumentException("Received token does not match token in the database");
                } else
                    throw new IllegalArgumentException(user.getUsername() + " has no tokens in the database");
            } else
                throw new IllegalArgumentException("Authorization bearer token has to start with \"Bearer \"");
        } else
            throw new IllegalArgumentException("Null authorization token");
    }

    public TokenEntity getTokensByUser(AppUser user){
        log.info("Getting tokens by user {}", user.getUsername());
        return tokenRepo.findByUser(user);
    }

    public void validateClient(Long clientId, String clientSecret){
        if (!clientId.equals(config.getClientId()) || !clientSecret.equals(config.getClientSecret()))
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid client credentials");
        log.info("Validated client credentials");
    }

    public TokenEntity getTokensByRefreshToken(String token){
        log.info("Fetching tokens by refresh token");
        return tokenRepo.findByRefreshToken(token);
    }

    public void saveTokens(TokenEntity tokens) {
        log.info("Saving tokens for {}", tokens.getUser().getUsername());
        tokenRepo.save(tokens);
    }

    public Collection<TokenEntity> getAllTokens(){
        log.info("Fetching all tokens");
        return tokenRepo.findAll();
    }
}
