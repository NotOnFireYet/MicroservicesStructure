package com.microsetvicetest.department.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsetvicetest.department.config.AppConfig;
import com.microsetvicetest.department.domain.ParamsTemplate;
import com.microsetvicetest.department.domain.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service @Slf4j
@AllArgsConstructor @NoArgsConstructor
public class DepartmentService {

    @Autowired
    private AppConfig config;

    @Autowired
    private RestTemplate restTemplate;

    private String URL;

    @PostConstruct
    public void postConstruct(){
        URL = config.getAdapterURL();
    }

    public String loginToAuth(User user){
        ParamsTemplate clientParams = new ParamsTemplate(config.getClientId(), config.getClientSecret());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> bodyMap = paramsToValueMap(user);
        MultiValueMap<String, String> clientMap = paramsToValueMap(clientParams);
        bodyMap.putAll(clientMap);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(bodyMap, headers);

        ResponseEntity<String> response = restTemplate.exchange(URL.concat("/login"), HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    public String fetchDepartment(String authHeader) {
        if (!authHeader.equals("")){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", authHeader);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(URL.concat("/department"), HttpMethod.GET, entity, String.class);
            return response.getBody();
        } else
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Empty authorization header");
    }

    // Converts an object into a map that can be passed as request payload
    private MultiValueMap<String, String> paramsToValueMap(Object params){
        ObjectMapper objectMapper = new ObjectMapper();
        MultiValueMap valueMap = new LinkedMultiValueMap<String, String>();
        Map<String, Object> fieldMap = objectMapper.convertValue(params, new TypeReference<>() {});
        valueMap.setAll(fieldMap);
        return valueMap;
    }
}
