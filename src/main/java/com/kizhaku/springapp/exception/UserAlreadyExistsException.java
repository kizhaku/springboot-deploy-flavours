package com.kizhaku.springapp.exception;

import com.kizhaku.springapp.model.ErrorMessages;
import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String userId;
    private final String error;
    private String message = "User %s already exists in system.";

    public UserAlreadyExistsException(String userId) {
        this.userId = userId;
        this.message = this.message.formatted(userId);
        this.error = ErrorMessages.USER_ALREADY_EXISTS.getError();
    }
}
