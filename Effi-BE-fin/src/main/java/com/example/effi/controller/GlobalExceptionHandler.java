package com.example.effi.controller;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
public class GlobalExceptionHandler {


//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<String> handleCustomException(CustomException ex) {
//        return ResponseEntity.status(400).body("Bad Request: " + ex.getMessage());
//    }

//    //404
//    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
//    public ResponseEntity<String> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found: " + ex.getMessage());
//    }
//
//
//    //500
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
//        return ResponseEntity.status(500).body("Internal Server Error: " + ex.getMessage());
//    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
    }
}
