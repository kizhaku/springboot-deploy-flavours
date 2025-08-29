package com.kizhaku.springapp.service;

import com.kizhaku.springapp.model.User;

public interface UserService {

    User addUser(User user);
    User findByuserId(String userId);
}
