package com.example.demo.service;

import com.example.demo.error.EmailAlreadyExistsException;
import com.example.demo.error.InvalidPasswordException;
import com.example.demo.error.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UserNotFoundException("User: " + username + " not found");
        return user;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User newUser) throws EmailAlreadyExistsException {
        if (EmailAlreadyExists(newUser.getEmail()))
            throw new EmailAlreadyExistsException("Email already used");

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);

    }


    // Standard login function
    public User loginUser(String email, String password) throws InvalidPasswordException {
        User user = findUserByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidPasswordException("Invalid credentials");

        return createSession(user);
    }

    // The session token may be used later to auto-connect the user when visiting the website if the token validity is still correct (e.g. 24h, 1 week...)
    // Equivalent to the "Stay connected" button
    public User createSession(User user) {
        UUID uuid = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        user.setSessionToken(uuid);
        user.setSessionTokenDate(date);
        userRepository.save(user);

        return user;
    }

    private boolean EmailAlreadyExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}


