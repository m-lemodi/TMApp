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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("testUser", "password123", "test@email.com");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void findUserByUsernameSuccess() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);

        User result = userService.findUserByUsername(testUser.getUsername());

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    void findUserByUsernameNotFound() {
        String nonexistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonexistentUsername)).thenReturn(null);

        assertThrows(UserNotFoundException.class, 
            () -> userService.findUserByUsername(nonexistentUsername));
        verify(userRepository).findByUsername(nonexistentUsername);
    }

//    @Test
//    void registerUserSuccess() {
//        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);
//        when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//        User result = userService.registerUser(testUser);
//
//        assertNotNull(result);
//        assertEquals(testUser.getUsername(), result.getUsername());
//        verify(passwordEncoder).encode(testUser.getPassword());
//        verify(userRepository).save(any(User.class));
//    }

    @Test
    void registerUserEmailExists() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        assertThrows(EmailAlreadyExistsException.class, 
            () -> userService.registerUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUserSuccess() {
        String rawPassword = "password123";
        testUser.setPassword("encodedPassword");
        
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.loginUser(testUser.getEmail(), rawPassword);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNotNull(result.getSessionToken());
        assertNotNull(result.getSessionTokenDate());
        verify(passwordEncoder).matches(rawPassword, testUser.getPassword());
    }

    @Test
    void loginUserInvalidPassword() {
        String wrongPassword = "wrongpassword";
        testUser.setPassword("encodedPassword");
        
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(wrongPassword, testUser.getPassword())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, 
            () -> userService.loginUser(testUser.getEmail(), wrongPassword));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUserNotFound() {
        String nonexistentEmail = "nonexistent@email.com";
        when(userRepository.findByEmail(nonexistentEmail)).thenReturn(null);

        assertThrows(InvalidPasswordException.class, 
            () -> userService.loginUser(nonexistentEmail, "anypassword"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createSessionSuccess() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createSession(testUser);

        assertNotNull(result);
        assertNotNull(result.getSessionToken());
        assertNotNull(result.getSessionTokenDate());
        assertEquals(LocalDate.now(), result.getSessionTokenDate());
        verify(userRepository).save(testUser);
    }

    @Test
    void createSessionUpdatesExistingUser() {
        testUser.setSessionToken(null);
        testUser.setSessionTokenDate(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createSession(testUser);

        assertNotNull(result.getSessionToken());
        assertNotNull(result.getSessionTokenDate());
        verify(userRepository).save(testUser);
    }
}