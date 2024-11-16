package com.dmh.account_service.configuration;

import com.dmh.account_service.exceptions.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new NotFoundException("Resource not found");
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}