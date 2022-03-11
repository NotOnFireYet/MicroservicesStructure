package com.microservicetest.authserver.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.domain.TokenEntity;
import com.microservicetest.authserver.service.TokenService;
import com.microservicetest.authserver.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Authenticating user {}", username);
        Long clientId = Long.valueOf(request.getParameter("client_id"));
        String clientSecret = request.getParameter("client_secret");
        try {
            tokenService.validateClient(clientId, clientSecret);
        } catch (Exception e){
            log.error("Authentication exception: {}", e.getMessage());
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("error_type", e.getClass().getSimpleName());
            responseMap.put("error_message", e.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), responseMap);
            throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
        throws IOException {
        User authUser = (User)authentication.getPrincipal();
        AppUser existingUser = userService.findByUsername(authUser.getUsername());
        log.info("Successfully authenticated {}", existingUser.getUsername());

        String accessToken = tokenService.generateAccessToken(existingUser);
        String refreshToken = tokenService.generateRefreshToken(existingUser);

        TokenEntity tokens = existingUser.getTokenEntity();
        tokens.setAccessToken(accessToken);
        tokens.setRefreshToken(refreshToken);

        tokens.setUser(existingUser);
        tokenService.saveTokens(tokens);

        existingUser.setTokenEntity(tokens);
        userService.saveUser(existingUser);

        log.info("Generated tokens for {}", tokens.getUser().getUsername());

        ObjectMapper mapper = new ObjectMapper();
        String tokensJson = mapper.writeValueAsString(tokens);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(tokensJson);
        out.flush();
    }
}