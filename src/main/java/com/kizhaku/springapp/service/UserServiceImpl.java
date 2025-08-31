package com.kizhaku.springapp.service;

import com.kizhaku.springapp.exception.UserNotFoundException;
import com.kizhaku.springapp.model.User;
import com.kizhaku.springapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepo;

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

    @Override
    public List<User> getAllUsers() {
        return userRepo.getAllUsers().orElseThrow(() -> new UserNotFoundException());
    }
}
