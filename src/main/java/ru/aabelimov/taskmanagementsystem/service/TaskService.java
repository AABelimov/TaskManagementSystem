package ru.aabelimov.taskmanagementsystem.service;

import org.springframework.security.core.Authentication;
import ru.aabelimov.taskmanagementsystem.dto.CreateTaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TasksDto;
import ru.aabelimov.taskmanagementsystem.entity.Task;

public interface TaskService {

    TaskDto createTask(CreateTaskDto dto, Authentication authentication);

    Task getTask(Long id);

    TasksDto getTasksByAuthorId(Long authorId, Integer page, String sort);

    TasksDto getTasksByPerformerId(Long performerId, Integer page, String sort);

    TaskDto updateTitle(Long id, String title);

    TaskDto updateDescription(Long id, String description);

    TaskDto updateStatus(Long id, String status);

    TaskDto updatePriority(Long id, String priority);

    TaskDto updatePerformer(Long id, String performerId);

    void deleteTask(Long id);
}
