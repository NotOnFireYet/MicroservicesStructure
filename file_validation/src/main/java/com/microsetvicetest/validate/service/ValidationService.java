package com.microsetvicetest.validate.service;

import com.microsetvicetest.validate.exception.CustomParsingErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;

@Slf4j
@Service
public class ValidationService implements IValidationService {
    // Called by the API method, gets the file through all the checks
    @Override
    public void validate(MultipartFile file) throws ParserConfigurationException {
        if (isXmlFileType(file)) {
            log.info("{} has a valid .xml extension", file.getOriginalFilename());
            if (isXmlSyntax(file))
                log.info("{} has valid XML syntax", file.getOriginalFilename());
            else
                throw new IllegalArgumentException("The file is not valid XML syntax");
        } else
            throw new IllegalArgumentException("Invalid file extension. XML files should end in .xml");
    }

    // Checks the validity of XML syntax by trying to parse it into an XML document
    @Override
    public Boolean isXmlSyntax(MultipartFile file) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new CustomParsingErrorHandler());
        try {
            InputStream input = file.getInputStream();
            builder.parse(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Checks if the file extension is .xml
    @Override
    public Boolean isXmlFileType(MultipartFile file) {
        try {
            return file.getOriginalFilename().endsWith(".xml");
        } catch (NullPointerException e){
            throw new IllegalArgumentException("The file name is null");
        }
    }
}
