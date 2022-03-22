package com.microsetvicetest.validate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsetvicetest.validate.config.AppConfig;
import com.microsetvicetest.validate.domain.ParamsTemplate;
import com.microsetvicetest.validate.domain.ResponseTemplate;
import com.microsetvicetest.validate.domain.User;
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
public class AuthService implements IAuthService{

    @Autowired
    private AppConfig config;

    @Autowired
    private RestTemplate restTemplate;

    private String URL;

    @PostConstruct
    public void postConstruct(){
        URL = config.getAdapterURL();
    }

    @Override
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

    // Checks if request is authorized by getting the status code of the auth server response
    public Boolean checkAuthorized(String authHeader) {
        if (!authHeader.equals("")){
            log.info("Authorizing request");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", authHeader);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            String response = restTemplate.exchange(URL.concat("/validate"), HttpMethod.GET, entity, String.class).getBody();
            return (response.equals("authorized"));
        } else
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Empty authorization header");
    }

    // Converts an object into a map that can be passed as request payload
    private static MultiValueMap<String, String> paramsToValueMap(Object params){
        ObjectMapper objectMapper = new ObjectMapper();
        MultiValueMap valueMap = new LinkedMultiValueMap<String, String>();
        Map<String, Object> fieldMap = objectMapper.convertValue(params, new TypeReference<>() {});
        valueMap.setAll(fieldMap);
        return valueMap;
    }
}
