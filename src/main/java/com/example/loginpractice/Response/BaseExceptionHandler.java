package com.example.loginpractice.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(BaseException e) {
        return ErrorResponse.toResponseEntity(e.getStatus());
    }
}
