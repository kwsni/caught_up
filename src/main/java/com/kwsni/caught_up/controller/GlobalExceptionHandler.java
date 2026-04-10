package com.kwsni.caught_up.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(HttpServletRequest req, Exception ex) {
        logger.error("request:" + req.getRequestURL() + " encountered an error ", ex);
        return "error";
    }
}
