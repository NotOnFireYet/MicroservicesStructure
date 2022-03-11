package com.microsetvicetest.department.config;

import com.microsetvicetest.department.service.DepartmentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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
