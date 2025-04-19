package com.example.demo.controller;

import com.example.demo.error.EmailAlreadyExistsException;
import com.example.demo.error.InvalidPasswordException;
import com.example.demo.error.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.io.IO.println;

@RestController
@RequestMapping("/users")
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

    @GetMapping("/register")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        User newUser = new User(username, password, email);
        try {
            User user = userService.registerUser(newUser);
            return new ResponseEntity<>(user.toString() + "has been registered successfully", HttpStatus.OK);}
        catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/login")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.loginUser(email, password);
            return new ResponseEntity<>("Welcome back " + user.getUsername() + "!", HttpStatus.OK);
        } catch (InvalidPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}
