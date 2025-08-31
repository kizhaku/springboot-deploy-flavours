package com.kizhaku.springapp.service;

import com.kizhaku.springapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User addUser(User user);
    User findByuserId(String userId);
    List<User> getAllUsers();
}
