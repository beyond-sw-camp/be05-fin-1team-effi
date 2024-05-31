package com.example.effi.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.effi.domain.Dto.Auth.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class ExceptionResponseHandler {
    
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse> handleSignatureException() {
        ApiResponse response = ApiResponse.builder()
                .msg("토큰이 유효하지 않습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse> handleMalformedException() {
        ApiResponse response = ApiResponse.builder()
                .msg("올바르지 않은 토큰입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException() {
        ApiResponse response = ApiResponse.builder()
                .msg("토큰이 만료되었습니다. 다시 로그인해주세요.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ApiResponse response = ApiResponse.builder()
                .msg("토큰이 존재하지 않거나 유효하지 않습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
