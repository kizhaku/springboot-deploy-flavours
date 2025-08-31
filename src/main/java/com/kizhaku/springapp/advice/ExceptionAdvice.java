package com.kizhaku.springapp.advice;

import com.kizhaku.springapp.dto.ErrorResponse;
import com.kizhaku.springapp.dto.UserErrorResponse;
import com.kizhaku.springapp.exception.UserAlreadyExistsException;
import com.kizhaku.springapp.exception.UserNotFoundException;
import com.kizhaku.springapp.model.ErrorMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserErrorResponse> userNotFoundExceptionResponse(UserNotFoundException ex) {
        return new ResponseEntity<>(
                new UserErrorResponse(ex.getUserId(), ex.getError(), ex.getMessage()),
                ErrorMessages.USER_NOT_FOUND.getStatus()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponse> userAlreadyExistsExceptionResponse(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new UserErrorResponse(ex.getUserId(), ex.getError(), ex.getMessage()),
                ErrorMessages.USER_ALREADY_EXISTS.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> validationExceptionResponse(MethodArgumentNotValidException ex) {
        List<ErrorResponse> errorList = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorList.add(new ErrorResponse(ErrorMessages.FIELD_VALIDATION_EXCEPTION.getError(),
                    error.getDefaultMessage()));
        });

        return new ResponseEntity<>(errorList,
                ErrorMessages.FIELD_VALIDATION_EXCEPTION.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> defaultExceptionResponse(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(ErrorMessages.DEFAULT_EXCEPTION.getError(),
                        ErrorMessages.DEFAULT_EXCEPTION.getError()),
                        ErrorMessages.DEFAULT_EXCEPTION.getStatus());
    }
}
