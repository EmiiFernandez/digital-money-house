package com.dmh.auth_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException {
    private String message;
}
