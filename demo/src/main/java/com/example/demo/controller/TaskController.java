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
    public ResponseEntity<String> addTask(@Valid @RequestBody AddTaskDTO addTaskDTO, @RequestHeader("user") User user) {
        try {
            taskService.addTask(new Task(addTaskDTO.getTitle(), addTaskDTO.getDescription(), addTaskDTO.getDueDate()), user);
            return new ResponseEntity<>("New task: " + addTaskDTO.getTitle() + "has been created successfully",
            HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("Error creating task: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editTask(@PathVariable String title, @RequestBody Task task, @RequestHeader("user") User user) {
        try {
//            Implement search by user and title
            taskService.editTask(title, task, user);
            return new ResponseEntity<>("Task: " + task.getTitle() + " has been updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating task: "+ title + ". Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public ResponseEntity<String> listTasksByUser(User user) {
        try {
            List<Task> tasks = taskService.getAllTasksForUser(user);
            return new ResponseEntity<>("Tasks: " + tasks.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error getting tasks for user: "+ user.getUsername() + ". Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String title, @RequestHeader("user") User user) {
        try {
//            Implement search by user and title
            Task task = taskService.deleteTask(title, user);
            return new ResponseEntity<>("Task "+ task.getTitle() + "has been deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting task: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
