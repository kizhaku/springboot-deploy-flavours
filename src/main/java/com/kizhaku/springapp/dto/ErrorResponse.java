package com.kizhaku.springapp.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ErrorResponse {
    private final String error;
    private final String message;
    private final Instant timeStamp;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timeStamp = Instant.now();
    }
}
