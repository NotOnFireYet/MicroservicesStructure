package com.microsetvicetest.validate.controller;

import com.microsetvicetest.validate.domain.ResponseTemplate;
import com.microsetvicetest.validate.service.AuthService;
import com.microsetvicetest.validate.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileAPI {
    @Autowired
    private ValidationService validationService;

    @Autowired
    private AuthService authService;

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam("files") MultipartFile[] files, @RequestHeader("Authorization") String authHeader)
        throws ParserConfigurationException {
        if (authService.checkAuthorized(authHeader)) {
            Map<String, ResponseTemplate> resultMap = new HashMap<>();
            for (MultipartFile file : files) {
                ResponseTemplate result = validateFile(file);
                resultMap.put(file.getOriginalFilename(), result);
            }
            return ResponseEntity.ok().body(resultMap);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseTemplate("error", "Failed to authorize"));
        }
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ResponseTemplate> catchEmptyHeaderException(HttpClientErrorException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseTemplate("error", "Empty authorization header"));
    }

    private ResponseTemplate validateFile(MultipartFile file)
        throws IllegalArgumentException, ParserConfigurationException {
        log.info("Validating {}", file.getOriginalFilename());
        try {
            validationService.validate(file);
            return new ResponseTemplate("success", "The file is a valid XML file!");
        } catch (IllegalArgumentException e) {
            return new ResponseTemplate("failed_validation", e.getMessage());
        }
    }
}
