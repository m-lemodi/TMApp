package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationDTO;
import com.example.demo.error.EmailAlreadyExistsException;
import com.example.demo.error.InvalidPasswordException;
import com.example.demo.error.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<String> getUser(@PathVariable String username) throws UserNotFoundException {
        try {
            User user = userService.findUserByUsername(username);
            return new ResponseEntity<>("User: " + user.getUsername(), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User(userRegistrationDTO.getUsername(), userRegistrationDTO.getPassword(), userRegistrationDTO.getEmail());

        try {
            User user = userService.registerUser(newUser);
            return new ResponseEntity<>(user.getUsername() + " has been registered successfully!", HttpStatus.OK);}
        catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.loginUser(email, password);
            return new ResponseEntity<>("Welcome back " + user.getUsername() + "!\nId: "+user.getId()+"\nSession token: "+user.getSessionToken(), HttpStatus.OK);
        } catch (InvalidPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}
