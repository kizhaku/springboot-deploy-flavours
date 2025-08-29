package com.kizhaku.springapp.mapper;

import com.kizhaku.springapp.dto.UserResponseDto;
import com.kizhaku.springapp.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(fullName(user.getFirstName(), user.getLastName()))")
    @Mapping(target = "userId", source = "user.id")
    UserResponseDto toResponse(User user);

    @AfterMapping
    default void fillBaseResponse(@MappingTarget UserResponseDto userResp) {
        userResp.setTimeStamp(Instant.now());
        userResp.setRequestId(UUID.randomUUID().toString());
    }

    default String fullName(String fName, String lName) {
        return fName + " " +lName;
    }
}
