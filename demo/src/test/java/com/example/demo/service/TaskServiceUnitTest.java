// src/test/java/com/example/demo/service/TaskServiceTest.java
package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.AddTaskDTO;
import com.example.demo.error.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private String validSessionToken;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password", "test@example.com");
        testUser.setId(1);
        validSessionToken = UUID.randomUUID().toString();
        testUser.setSessionToken(UUID.fromString(validSessionToken));
        testUser.setSessionTokenDate(LocalDate.now());

        testTask = new Task("Test Task", "Test Description", LocalDate.now().plusDays(1));
        testTask.setOwner(testUser);
    }

    @Test
    void addTask_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByTitleAndOwner(anyString(), any(User.class))).thenReturn(null);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.addTask(testTask, "1", validSessionToken);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(testUser, result.getOwner());
        verify(taskRepository).save(any(Task.class));
    }

//    @Test
//    void addTask_TaskAlreadyExists() {
//        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
//        when(taskRepository.findByTitleAndOwner("Test Task", testUser)).thenReturn(testTask);
//
//        assertThrows(TaskAlreadyExistsException.class, () ->
//            taskService.addTask(testTask, "1", validSessionToken)
//        );
//    }

    @Test
    void editTask_Success() {
        AddTaskDTO updateDTO = new AddTaskDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setDueDate(LocalDate.now().plusDays(2));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByTitleAndOwner("Test Task", testUser)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.editTask("Test Task", updateDTO, "1", validSessionToken);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByTitleAndOwner("Test Task", testUser)).thenReturn(testTask);
        doNothing().when(taskRepository).delete(any(Task.class));

        Task result = taskService.deleteTask("Test Task", "1", validSessionToken);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository).delete(testTask);
    }

    @Test
    void changeTaskStatus_Success() {
        testTask.setStatus("PENDING");
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByTitleAndOwner("Test Task", testUser)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.changeTaskStatus("Test Task", "1", validSessionToken);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(taskRepository).save(testTask);
    }

    @Test
    void getAllTasksForUser_Success() {
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByOwner(testUser)).thenReturn(expectedTasks);

        List<Task> result = taskService.getAllTasksForUser("1", validSessionToken);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
    }

    @Test
    void invalidSessionToken_ThrowsException() {
        testUser.setSessionTokenDate(LocalDate.now().minusDays(2));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () ->
            taskService.getAllTasksForUser("1", validSessionToken)
        );
    }
}