package com.kizhaku.springapp.dto;

import lombok.Getter;

@Getter
public class UserErrorResponse extends ErrorResponse {
    private final String userId;

    public UserErrorResponse(String userId, String error, String message) {
        super(error, message);
        this.userId = userId;
    }
}
