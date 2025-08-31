package com.kizhaku.springapp.advice;

import com.kizhaku.springapp.dto.ErrorResponseDto;
import com.kizhaku.springapp.dto.UserErrorResponseDto;
import com.kizhaku.springapp.exception.UserAlreadyExistsException;
import com.kizhaku.springapp.exception.UserNotFoundException;
import com.kizhaku.springapp.model.ErrorMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponseDto> userNotFoundExceptionResponse(UserNotFoundException ex) {
        return new ResponseEntity<>(
                new UserErrorResponseDto(ex.getUserId(), ex.getError(), ex.getMessage()),
                ErrorMessages.USER_NOT_FOUND.getStatus()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponseDto> userAlreadyExistsExceptionResponse(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new UserErrorResponseDto(ex.getUserId(), ex.getError(), ex.getMessage()),
                ErrorMessages.USER_ALREADY_EXISTS.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> defaultExceptionResponse(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponseDto(ErrorMessages.DEFAULT_EXCEPTION.getError(),
                        ErrorMessages.DEFAULT_EXCEPTION.getError()),
                        ErrorMessages.DEFAULT_EXCEPTION.getStatus());
    }
}
