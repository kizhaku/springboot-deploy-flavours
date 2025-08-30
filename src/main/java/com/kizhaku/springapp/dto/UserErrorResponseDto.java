package com.kizhaku.springapp.dto;

import lombok.Getter;

@Getter
public class UserErrorResponseDto extends ErrorResponseDto {
    private final String userId;

    public UserErrorResponseDto(String userId, String error, String message) {
        super(error, message);
        this.userId = userId;
    }
}
