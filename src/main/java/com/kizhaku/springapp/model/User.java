package com.kizhaku.springapp.model;

import lombok.Getter;
import java.util.UUID;

@Getter
public class User {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String someVar = "This is a test var";

    public User(String firstName, String lastName) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
