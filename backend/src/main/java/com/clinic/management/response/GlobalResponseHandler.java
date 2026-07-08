package com.clinic.management.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.clinic.management.controller")
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply only if the return type is not already ApiResponse or ErrorResponse
        Class<?> type = returnType.getParameterType();
        return !type.equals(ApiResponse.class) && !type.equals(ErrorResponse.class) && !type.equals(byte[].class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            // Special handling for String since it uses StringHttpMessageConverter
            return body;
        }
        
        // Wrap response in generic ApiResponse
        return ApiResponse.success("Request processed successfully", body);
    }
}
