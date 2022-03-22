package com.microsetvicetest.validate.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsetvicetest.validate.domain.ResponseTemplate;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionResolver implements HandlerExceptionResolver {
    @SneakyThrows
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        ModelAndView modelAndView = new ModelAndView("file");
        if (e instanceof MaxUploadSizeExceededException) {
            new ObjectMapper().writeValue(response.getOutputStream(), new ResponseTemplate("error", "Maximum file size exceeded"));
        }
        return modelAndView;
    }
}
