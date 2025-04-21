package com.example.demo.controller;

import com.example.demo.dto.AddTaskDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<String> addTask(@Valid @RequestBody AddTaskDTO addTaskDTO, @RequestHeader("x-user-id") String userId, @RequestHeader("x-session-token") String sessionToken) {
        try {
            Task task = taskService.addTask(new Task(addTaskDTO.getTitle(), addTaskDTO.getDescription(), addTaskDTO.getDueDate()), userId, sessionToken);
            return new ResponseEntity<>("New task: " + task.getTitle() + "has been created successfully",
            HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating task: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{title}")
    public ResponseEntity<String> editTask(@PathVariable String title, @RequestBody AddTaskDTO taskDTO, @RequestHeader("x-user-id") String userId, @RequestHeader("x-session-token") String sessionToken) {
        try {
            Task task = taskService.editTask(title, taskDTO, userId, sessionToken);
            return new ResponseEntity<>("Task: " + task.getTitle() + " has been updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating task: "+ title + ". Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public ResponseEntity<List<Task>> listTasksByUser(@RequestHeader("x-user-id") String userId, @RequestHeader("x-session-token") String sessionToken) {
        try {
            List<Task> tasks = taskService.getAllTasksForUser(userId, sessionToken);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<String> deleteTask(@PathVariable String title, @RequestHeader("x-user-id") String userId, @RequestHeader("x-session-token") String sessionToken) {
        try {
//            Implement search by user and title
            Task task = taskService.deleteTask(title, userId, sessionToken);
            return new ResponseEntity<>("Task "+ task.getTitle() + "has been deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting task: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/{title}/complete")
    public ResponseEntity<String> completeTask(@PathVariable String title, @RequestHeader("x-user-id") String userId, @RequestHeader("x-session-token") String sessionToken) {
        try {
            Task task = taskService.completeTask(title, userId, sessionToken);
            return new ResponseEntity<>("Task: " + task.getTitle() + " has been marked as completed", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error completing task: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
