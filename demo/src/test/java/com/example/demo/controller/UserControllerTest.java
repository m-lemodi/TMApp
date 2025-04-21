package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationDTO;
import com.example.demo.error.EmailAlreadyExistsException;
import com.example.demo.error.InvalidPasswordException;
import com.example.demo.error.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserSuccess() throws UserNotFoundException {
        // Arrange
        String username = "testUser";
        User mockUser = new User(username, "password", "test@email.com");
        when(userService.findUserByUsername(username)).thenReturn(mockUser);

        // Act
        ResponseEntity<String> response = userController.getUser(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User: " + username, response.getBody());
    }

    @Test
    void getUserNotFound() throws UserNotFoundException {
        // Arrange
        String username = "nonexistentUser";
        when(userService.findUserByUsername(username))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act
        ResponseEntity<String> response = userController.getUser(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void registerUserSuccess() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("newUser");
        dto.setEmail("new@email.com");
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        User mockUser = new User(dto.getUsername(), dto.getPassword(), dto.getEmail());
        when(userService.registerUser(any(User.class))).thenReturn(mockUser);

        // Act
        ResponseEntity<String> response = userController.register(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("has been registered successfully!"));
    }

    @Test
    void registerUserPasswordMismatch() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("newUser");
        dto.setEmail("new@email.com");
        dto.setPassword("password1");
        dto.setConfirmPassword("password2");

        // Act
        ResponseEntity<String> response = userController.register(dto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Passwords do not match", response.getBody());
    }

    @Test
    void registerUserEmailExists() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("newUser");
        dto.setEmail("existing@email.com");
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        when(userService.registerUser(any(User.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already used"));

        // Act
        ResponseEntity<String> response = userController.register(dto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already used", response.getBody());
    }

    @Test
    void loginSuccess() {
        // Arrange
        String email = "test@email.com";
        String password = "password";
        User mockUser = new User("testUser", password, email);
        when(userService.loginUser(email, password)).thenReturn(mockUser);

        // Act
        ResponseEntity<?> response = userController.login(email, password);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void loginInvalidCredentials() {
        // Arrange
        String email = "test@email.com";
        String password = "wrongpassword";
        when(userService.loginUser(email, password))
                .thenThrow(new InvalidPasswordException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = userController.login(email, password);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }
}