package com.kizhaku.springapp.controller;


import com.kizhaku.springapp.dto.UserRequest;
import com.kizhaku.springapp.dto.UserResponse;
import com.kizhaku.springapp.mapper.UserResponseMapper;
import com.kizhaku.springapp.model.User;
import com.kizhaku.springapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
@Tag(name = "App Controller")
public class AppController {
    private final UserService userService;
    private final UserResponseMapper userMapper;

    public AppController(UserService userService, UserResponseMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("hello")
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<>("Hello world!", HttpStatus.OK);
    }

    @PostMapping("user")
    public ResponseEntity<UserResponse> addUser(@RequestBody @Valid UserRequest request) {
        User user = new User(request.getFirstName(), request.getLastName());
        User addedUser = userService.addUser(user);
        return new ResponseEntity<>(userMapper.toResponse(addedUser), HttpStatus.CREATED);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userService.findByuserId(id);
        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }

    @GetMapping("users")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
