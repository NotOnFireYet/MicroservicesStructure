package com.microservicetest.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicetest.user.domain.ParamsTemplate;
import com.microservicetest.user.domain.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.Map;

public interface IUserService {
    String getTokensOnLogin(ParamsTemplate params);

    public String checkAuthorized(String authHeader);

    public User saveUser(User user);

    public Collection<User> getAllUsers();

    String getActiveUsername(String authToken);

    // Converts an object into a map that can be passed as request payload
    default MultiValueMap<String, String> paramsToValueMap(Object params){
        ObjectMapper objectMapper = new ObjectMapper();
        MultiValueMap valueMap = new LinkedMultiValueMap<String, String>();
        Map<String, Object> fieldMap = objectMapper.convertValue(params, new TypeReference<>() {});
        valueMap.setAll(fieldMap);
        return valueMap;
    }
}
