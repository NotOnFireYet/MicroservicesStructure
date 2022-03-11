package com.microservicetest.user.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Value("${auth.url}")
    private String authURL;
}