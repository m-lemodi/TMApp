// src/test/java/com/example/demo/controller/TaskControllerTest.java
package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import com.example.demo.dto.AddTaskDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;
    private AddTaskDTO testTaskDTO;
    private String userId;
    private String sessionToken;

    @BeforeEach
    void setUp() {
        userId = "1";
        sessionToken = "valid-session-token";
        
        testTask = new Task("Test Task", "Test Description", LocalDate.now().plusDays(1));
        
        testTaskDTO = new AddTaskDTO();
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test Description");
        testTaskDTO.setDueDate(LocalDate.now().plusDays(1));
    }

    @Test
    void addTask_Success() throws Exception {
        when(taskService.addTask(any(Task.class), anyString(), anyString()))
            .thenReturn(testTask);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskDTO))
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("New task: %shas been created successfully", 
                    testTask.getTitle())));
    }

    @Test
    void editTask_Success() throws Exception {
        when(taskService.editTask(anyString(), any(AddTaskDTO.class), anyString(), anyString()))
            .thenReturn(testTask);

        mockMvc.perform(put("/tasks/{title}", "Test Task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskDTO))
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Task: %s has been updated successfully", 
                    testTask.getTitle())));
    }

    @Test
    void deleteTask_Success() throws Exception {
        when(taskService.deleteTask(anyString(), anyString(), anyString()))
            .thenReturn(testTask);

        mockMvc.perform(delete("/tasks/{title}", "Test Task")
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Task %shas been deleted successfully", 
                    testTask.getTitle())));
    }

    @Test
    void changeTaskStatus_Success() throws Exception {
        testTask.setStatus("COMPLETED");
        when(taskService.changeTaskStatus(anyString(), anyString(), anyString()))
            .thenReturn(testTask);

        mockMvc.perform(put("/tasks/{title}/status", "Test Task")
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Task: %s has been marked as %s", 
                    testTask.getTitle(), testTask.getStatus())));
    }

    @Test
    void listTasksByUser_Success() throws Exception {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.getAllTasksForUser(anyString(), anyString()))
            .thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    void searchTasks_Success() throws Exception {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.getAllTasksForUser(anyString(), anyString()))
            .thenReturn(tasks);

        mockMvc.perform(get("/tasks/search")
                .param("query", "Test")
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void addTask_InvalidInput() throws Exception {
        testTaskDTO.setTitle(null); // Invalid input

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskDTO))
                .header("x-user-id", userId)
                .header("x-session-token", sessionToken))
                .andExpect(status().isBadRequest());
    }
}