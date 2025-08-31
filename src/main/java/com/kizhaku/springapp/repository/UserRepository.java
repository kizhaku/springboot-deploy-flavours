package com.kizhaku.springapp.repository;

import com.kizhaku.springapp.exception.UserAlreadyExistsException;
import com.kizhaku.springapp.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    public User addUser(User user) {
        if(users.containsKey(user.getId()))
            throw new UserAlreadyExistsException(user.getId());

        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findByuserId(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<List<User>> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());

        return userList.isEmpty() ? Optional.empty() : Optional.of(userList);
    }
}
