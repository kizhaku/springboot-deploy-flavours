package com.kizhaku.springapp.service;

import com.kizhaku.springapp.exception.UserNotFoundException;
import com.kizhaku.springapp.model.User;
import com.kizhaku.springapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User addUser(User user) {
        return userRepo.addUser(user);
    }

    @Override
    public User findByuserId(String userId) {
        return userRepo.findByuserId(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
