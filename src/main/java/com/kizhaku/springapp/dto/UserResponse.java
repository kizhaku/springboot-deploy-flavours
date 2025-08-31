package com.kizhaku.springapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends ApiResponse {
    private String userId;
    private String fullName;
}
