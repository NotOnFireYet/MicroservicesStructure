package com.microsetvicetest.validate.exception;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

@Slf4j
public class CustomParsingErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException e) {
        log.info("File could not be parsed. {}", e.getMessage());
    }

    @Override
    public void error(SAXParseException e) {
        log.info("File could not be parsed. {}", e.getMessage());
    }

    @Override
    public void fatalError(SAXParseException e) {
        log.info("File could not be parsed. {}", e.getMessage());
    }
}