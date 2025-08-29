package com.kizhaku.springapp.exception;

public class UserNotFoundException extends RuntimeException {

    private String userId;

    public UserNotFoundException() {};

    public UserNotFoundException(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
