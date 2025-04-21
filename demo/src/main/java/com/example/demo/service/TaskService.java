package com.example.demo.service;

import com.example.demo.dto.AddTaskDTO;
import com.example.demo.error.*;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task addTask(Task task, String userId, String sessionToken) throws IllegalArgumentException, TaskAlreadyExistsException {

        try {
            User user = checkUserAndSessionToken(userId, sessionToken);

            // Check if a task already exists
            if (taskRepository.findByTitleAndOwner(task.getTitle(), user) != null) {
                throw new TaskAlreadyExistsException("Task already exists: " + task.getTitle());
            }

            task.setOwner(user);
            taskRepository.save(task);
            return task;

        } catch (InvalidSessionTokenException e) {
            throw new IllegalArgumentException("Invalid session token");
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("User not found: " + userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error adding task: " + task.getTitle() + ". Error: " + e.getMessage());
        }

    }

    public Task editTask(String title, AddTaskDTO task, String userId, String sessionToken) throws IllegalArgumentException, TaskNotFoundException, InvalidOwnerException {
        if (title == null || task == null) {
            throw new IllegalArgumentException("Title and task cannot be null");
        }

        try {
            User user = checkUserAndSessionToken(userId, sessionToken);

            Task existingTask = taskRepository.findByTitleAndOwner(title, user);
            if (existingTask == null) throw new TaskNotFoundException("Task not found: " + title);

            if (!existingTask.getOwner().equals(user)) {
                throw new InvalidOwnerException("User is not authorized to edit this task");
            }

            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setDueDate(task.getDueDate());

            taskRepository.save(existingTask);
            return existingTask;

        } catch (InvalidSessionTokenException e) {
            throw new IllegalArgumentException("Invalid session token");
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("User not found: " + userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error editing task: " + title + ". Error: " + e.getMessage());
        }
    }

    public Task deleteTask(String title, String userId, String sessionToken) throws IllegalArgumentException, TaskNotFoundException, InvalidOwnerException {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        try {
            User user = checkUserAndSessionToken(userId, sessionToken);

            Task existingTask = taskRepository.findByTitleAndOwner(title, user);

            if (existingTask == null) throw new TaskNotFoundException("Task not found: " + title);

            if (!existingTask.getOwner().equals(user)) {
                throw new InvalidOwnerException("User is not authorized to delete this task");
            }

            taskRepository.delete(existingTask);
            return existingTask;

        } catch (InvalidSessionTokenException e) {
            throw new IllegalArgumentException("Invalid session token");
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("User not found: " + userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting task: " + title + ". Error: " + e.getMessage());
        }


    }

    public Task changeTaskStatus(String title, String userId, String sessionToken) throws IllegalArgumentException, TaskNotFoundException, InvalidOwnerException {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        try {
            User user = checkUserAndSessionToken(userId, sessionToken);
            Task existingTask = taskRepository.findByTitleAndOwner(title, user);
            if (existingTask == null) throw new TaskNotFoundException("Task not found: " + title);
            if (!existingTask.getOwner().equals(user)) {
                throw new InvalidOwnerException("User is not authorized to complete this task");
            }

            if (existingTask.getStatus().equals("COMPLETED")) {
                existingTask.setStatus("PENDING");
            } else existingTask.setStatus("COMPLETED");

            taskRepository.save(existingTask);
            return existingTask;

        } catch (InvalidSessionTokenException e) {
            throw new IllegalArgumentException("Invalid session token");
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("User not found: " + userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error completing task: " + title + ". Error: " + e.getMessage());
        }
    }

    public List<Task> getAllTasksForUser(String userId, String sessionToken) throws IllegalArgumentException {
        try {
            User user = checkUserAndSessionToken(userId, sessionToken);
            return taskRepository.findByOwner(user);

        } catch (InvalidSessionTokenException e) {
            throw new IllegalArgumentException("Invalid session token");
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("User not found: " + userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error getting tasks for user: " + userId + ". Error: " + e.getMessage());
        }

    }

    private User checkUserAndSessionToken(String userId, String sessionToken) throws InvalidSessionTokenException, UserNotFoundException{
        // Retrieve user from database
        User user = userRepository.findById(Integer.parseInt(userId)).orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        // Session token check
        if (!user.getSessionToken().toString().equals(sessionToken) || user.getSessionTokenDate().plusDays(1).isBefore(LocalDate.now())) {
            throw new InvalidSessionTokenException("Invalid session token");
        }
        return user;
    }
}
