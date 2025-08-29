package com.kizhaku.springapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 *  Base class for API response. Will keep the minimum here and others can extend it.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
    String requestId;
    Instant timeStamp;
}
