package com.kizhaku.springapp.exception;

import com.kizhaku.springapp.model.ErrorMessages;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private String userId;
    private String error;
    private String message = "User %s not found in system.";

    public UserNotFoundException() {
        this.error = ErrorMessages.USERS_EMPTY.getError();
        this.message = ErrorMessages.USERS_EMPTY.getError();
    };

    public UserNotFoundException(String userId) {
        this.userId = userId;
        this.error = ErrorMessages.USER_NOT_FOUND.getError();
        this.message = this.message.formatted(userId);
    }
}
