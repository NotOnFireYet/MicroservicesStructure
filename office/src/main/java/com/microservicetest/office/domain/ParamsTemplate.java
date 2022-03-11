package com.microservicetest.office.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamsTemplate {
    Long client_id;
    String client_secret;
}
