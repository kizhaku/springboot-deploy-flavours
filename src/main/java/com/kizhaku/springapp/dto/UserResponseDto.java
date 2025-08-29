package com.kizhaku.springapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto extends ApiResponseDto {
    private String userId;
    private String fullName;
}
