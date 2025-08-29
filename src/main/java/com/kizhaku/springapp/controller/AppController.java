package com.kizhaku.springapp.controller;


import com.kizhaku.springapp.dto.UserResponseDto;
import com.kizhaku.springapp.mapper.UserMapper;
import com.kizhaku.springapp.model.User;
import com.kizhaku.springapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@Tag(name = "App Controller")
public class AppController {

    private final UserService userService;
    private final UserMapper userMapper;

    public AppController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("hello")
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<>("Hello there!", HttpStatus.OK);
    }

    @PostMapping("user")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody User user) {
        User addedUser = userService.addUser(user);
        return new ResponseEntity<>(userMapper.toResponse(addedUser), HttpStatus.CREATED);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userService.findByuserId(id);

        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }
}
