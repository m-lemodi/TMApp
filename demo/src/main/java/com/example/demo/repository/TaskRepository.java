package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    Task findByTitle(String title);
    Task findByDescription(String description);
    Task findByTitleAndOwner(String title, User user);

    List<Task> findByOwner(User user);
}
