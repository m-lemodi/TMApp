package com.example.demo.controller;

import com.example.demo.dto.LoginResponseDTO;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("testUser", "password123", "test@email.com");
        testUser.setId(1);
        testUser.setSessionToken(UUID.randomUUID());

        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testUser");
        registrationDTO.setEmail("test@email.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setConfirmPassword("password123");
    }

    @Test
    void getUserSuccess() {
        when(userService.findUserByUsername(testUser.getUsername())).thenReturn(testUser);

        ResponseEntity<String> response = userController.getUser(testUser.getUsername());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(testUser.getUsername()));
        verify(userService).findUserByUsername(testUser.getUsername());
    }

    @Test
    void getUserNotFound() {
        when(userService.findUserByUsername(anyString()))
                .thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<String> response = userController.getUser("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).findUserByUsername("nonexistent");
    }

    @Test
    void registerUserSuccess() {
        when(userService.registerUser(any(User.class))).thenReturn(testUser);

        ResponseEntity<String> response = userController.register(registrationDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("registered successfully"));
        verify(userService).registerUser(any(User.class));
    }

    @Test
    void registerUserPasswordMismatch() {
        registrationDTO.setConfirmPassword("differentPassword");

        ResponseEntity<String> response = userController.register(registrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Passwords do not match", response.getBody());
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void registerUserEmailExists() {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already used"));

        ResponseEntity<String> response = userController.register(registrationDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already used", response.getBody());
        verify(userService).registerUser(any(User.class));
    }

    @Test
    void loginSuccess() {
        when(userService.loginUser(testUser.getEmail(), "password123")).thenReturn(testUser);

        ResponseEntity<?> response = userController.login(testUser.getEmail(), "password123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponseDTO);
        LoginResponseDTO loginResponse = (LoginResponseDTO) response.getBody();
        assertEquals(testUser.getId(), loginResponse.getUserId());
        assertEquals(testUser.getUsername(), loginResponse.getUsername());
        assertEquals(testUser.getSessionToken().toString(), loginResponse.getSessionToken());
        verify(userService).loginUser(testUser.getEmail(), "password123");
    }

    @Test
    void loginInvalidCredentials() {
        when(userService.loginUser(anyString(), anyString()))
                .thenThrow(new InvalidPasswordException("Invalid credentials"));

        ResponseEntity<?> response = userController.login("test@email.com", "wrongpassword");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
        verify(userService).loginUser("test@email.com", "wrongpassword");
    }
}