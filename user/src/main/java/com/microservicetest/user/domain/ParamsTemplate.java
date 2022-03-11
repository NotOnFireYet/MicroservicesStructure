package com.microservicetest.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamsTemplate {
    String username;
    String password;
    Long client_id;
    String client_secret;
}
