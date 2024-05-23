package com.example.effi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
    private final HandlerExceptionResolver resolver;

     public JwtAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }
 
    // @Override
    // public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

    //     resolver.resolveException(request, response, null, (Exception) request.getAttribute("exception"));

    // }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        // Get the exception from the request attribute
        Exception exception = (Exception) request.getAttribute("exception");
        // If the exception is null, set a default exception
        if (exception == null) {
            exception = authException != null ? authException : new Exception("Unknown error occurred");
        }
        // Resolve the exception
        resolver.resolveException(request, response, null, exception);
}

}
