package com.microservicetest.office.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Value("${client.secret}")
    private String clientSecret;

    @Value("${client.id}")
    private Long clientId;

    @Value("${adapter.url}")
    private String adapterURL;
}
