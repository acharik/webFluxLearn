package com.reactivespring.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
     @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleWebExchangeBindException(WebExchangeBindException  e) {
         log.error(e.getMessage());
         var error = e.getBindingResult().getAllErrors().stream()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .sorted()
                 .collect(Collectors.joining(","));
         log.error(error);
         return ResponseEntity.badRequest().body(error);
     }
}
