package com.kizhaku.springapp.service;

import com.kizhaku.springapp.exception.UserNotFoundException;
import com.kizhaku.springapp.model.User;
import com.kizhaku.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit tests")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepo;

    @InjectMocks
    UserServiceImpl userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void init() {
        testUser = new User("Kittu", "Meow");
        testUsers = List.of(new User("Kittu", "Meow"),
                new User("Ringo", "Bow"),
                new User("Owly", "Woo"));
    }

    @Test
    @DisplayName("Add user successful")
    public void testAddUser_whenUserProvided_shouldBeSuccessful() {
        Mockito.when(userRepo.addUser(testUser)).thenReturn(testUser);
        User user = userService.addUser(testUser);

        assertEquals(testUser.getFirstName(), user.getFirstName(),
                () -> "User add failed");

        assertEquals(testUser.getLastName(), user.getLastName(),
                () -> "User add failed");
    }

    @Test
    @DisplayName("Get user successful")
    public void testFindUserById_whenUserExists_shouldBeSuccessful() {
        Mockito.when(userRepo.findByuserId(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        User user = userService.findByuserId(testUser.getId());

        Mockito.verify(userRepo).findByuserId(testUser.getId());
        assertEquals(user.getId(), testUser.getId());
    }

    @Test
    @DisplayName("User not found should throw exception")
    public void testFindUserById_whenUserDoesntExist_throwsUserNotFoundException() {
        Mockito.when(userRepo.findByuserId(Mockito.any()))
                .thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.findByuserId("123abc"));

        Mockito.verify(userRepo).findByuserId("123abc");
        assertTrue(ex.getMessage().contains("123abc"));
    }

    @Test
    @DisplayName("Should return list of users")
    public void testGetAllUsers_whenUsersExists_shouldReturnListOfUsers() {
        Mockito.when(userRepo.getAllUsers())
                .thenReturn(Optional.of(testUsers));

        List<User> users = userService.getAllUsers();

        assertEquals(users.get(0).getId(), testUsers.get(0).getId());
        assertEquals(users.get(0).getFirstName(), testUsers.get(0).getFirstName());
        assertEquals(users.get(1).getId(), testUsers.get(1).getId());
        assertEquals(users.get(1).getId(), testUsers.get(1).getId());
    }

    @Test
    @DisplayName("Get all users should throw error when no user found")
    public void testGetAllUsers_whenNoUserExists_shouldThrowUserNotFoundException() {
        Mockito.when(userRepo.getAllUsers())
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getAllUsers());
    }
}
