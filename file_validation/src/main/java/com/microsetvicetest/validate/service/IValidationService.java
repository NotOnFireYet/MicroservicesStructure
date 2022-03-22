package com.microsetvicetest.validate.service;

import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface IValidationService {
    void validate(MultipartFile file) throws IOException, SAXException, ParserConfigurationException;
    Boolean isXmlSyntax(MultipartFile file) throws ParserConfigurationException, IOException, SAXException;
    Boolean isXmlFileType(MultipartFile file);
}
