package com.example.demo.service;

import com.example.demo.error.EmailAlreadyExistsException;
import com.example.demo.error.InvalidPasswordException;
import com.example.demo.error.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserByUsernameSuccess() {
        // Arrange
        String username = "testUser";
        User mockUser = new User(username, "password", "test@email.com");
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        // Act
        User result = userService.findUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void findUserByUsernameNotFound() {
        // Arrange
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername(username));
    }

    @Test
    void registerUserSuccess() {
        // Arrange
        User newUser = new User("newUser", "password", "new@email.com");
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.registerUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals(newUser.getUsername(), result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserEmailExists() {
        // Arrange
        User existingUser = new User("existingUser", "password", "existing@email.com");
        User newUser = new User("newUser", "password", "existing@email.com");
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(existingUser);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(newUser));
        verify(userRepository, never()).save(any(User.class));
    }

//    @Test
//    void loginUserSuccess() {
//        // Arrange
//        String email = "test@email.com";
//        String rawPassword = "password";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//        User mockUser = new User("testUser", rawPassword, email);
//        when(userRepository.findByEmail(email)).thenReturn(mockUser);
//        when(userRepository.save(any(User.class))).thenReturn(mockUser);
//
//        // Act
//        User result = userService.loginUser(email, rawPassword);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(email, result.getEmail());
//        assertNotNull(result.getSessionToken());
//    }

    @Test
    void loginUserInvalidPassword() {
        // Arrange
        String email = "test@email.com";
        String password = "wrongpassword";
        User mockUser = new User("testUser", "correctpassword", email);
        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        // Act & Assert
        assertThrows(InvalidPasswordException.class, () -> userService.loginUser(email, password));
    }

    @Test
    void createSessionSuccess() {
        // Arrange
        User user = new User("testUser", "password", "test@email.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createSession(user);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSessionToken());
        assertNotNull(result.getSessionTokenDate());
        verify(userRepository).save(user);
    }
}