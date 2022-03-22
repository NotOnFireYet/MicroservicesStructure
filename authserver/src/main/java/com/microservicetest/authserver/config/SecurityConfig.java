package com.microservicetest.authserver.config;

import com.microservicetest.authserver.filter.CustomAuthenticationFilter;
import com.microservicetest.authserver.filter.CustomAuthorizationFilter;
import com.microservicetest.authserver.service.TokenService;
import com.microservicetest.authserver.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Data
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService userDetailsService;

    @Value("${client.secret}")
    private String clientSecret;

    @Value("${client.id}")
    private Long clientId;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilter(new CustomAuthenticationFilter(authenticationManagerBean(), tokenServiceBean(), userServiceBean()))
            .addFilterBefore(new CustomAuthorizationFilter(tokenServiceBean()), UsernamePasswordAuthenticationFilter.class)
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(STATELESS);

        http.authorizeRequests().antMatchers().permitAll();
        http.authorizeRequests().antMatchers("/user/validate/**").authenticated();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationManager();
    }

    @Bean
    public TokenService tokenServiceBean() {
        return new TokenService();
    }

    @Bean
    public UserService userServiceBean() {
        return new UserService();
    }

}
