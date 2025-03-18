package com.recruitment.task.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            Map<String, String> error = new HashMap<>();
            String fieldName = violation.getPropertyPath().toString();
            String paramName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            error.put("field", paramName);
            error.put("message", violation.getMessage());
            errors.add(error);
        });
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleMissingParameter(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("field", ex.getParameterName());
        error.put("message", "Required request parameter '" + ex.getParameterName() + "' is not present");
        List<Map<String, String>> errors = List.of(error);
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        String paramName = ex.getName();
        Object paramValueObj = ex.getValue();
        String paramValue = paramValueObj != null ? String.valueOf(paramValueObj) : "null";
        error.put("field", paramName);
        error.put("message", "Invalid value '" + paramValue + "' for parameter '" + paramName + "'");
        List<Map<String, String>> errors = List.of(error);
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}