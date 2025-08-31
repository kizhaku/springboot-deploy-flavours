package com.kizhaku.springapp.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessages {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found in system"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists in system"),
    DEFAULT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occured in server");

    private final String error;
    private final HttpStatus status;

    ErrorMessages(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
    }
}
