package ru.aabelimov.taskmanagementsystem.service.impl;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.aabelimov.taskmanagementsystem.dto.CreateTaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TasksDto;
import ru.aabelimov.taskmanagementsystem.entity.Task;
import ru.aabelimov.taskmanagementsystem.entity.TaskPriority;
import ru.aabelimov.taskmanagementsystem.entity.TaskStatus;
import ru.aabelimov.taskmanagementsystem.entity.User;
import ru.aabelimov.taskmanagementsystem.exception.TaskNotFoundException;
import ru.aabelimov.taskmanagementsystem.mapper.TaskMapper;
import ru.aabelimov.taskmanagementsystem.repository.TaskRepository;
import ru.aabelimov.taskmanagementsystem.service.TaskService;
import ru.aabelimov.taskmanagementsystem.service.UserService;

@Service
@RequiredArgsConstructor
public class TaskServiceDefaultImpl implements TaskService {

    public static final int PAGE_SIZE = 5;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    @Override
    public TaskDto createTask(CreateTaskDto dto, Authentication authentication) {
        Task task = taskMapper.toEntity(dto);
        task.setAuthor((User) userService.loadUserByUsername(authentication.getName()));
        task.setPerformer(userService.getUser(dto.performerId()));
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public TasksDto getTasksByAuthorId(Long authorId, Integer page, String sort) {
        page = page < 0 ? 0 : page;
        PageRequest pageRequest = createPageRequest(page, sort);
        User user = userService.getUser(authorId);
        return taskMapper.toListDto(taskRepository.findAllByAuthor(user, pageRequest));
    }

    @Override
    public TasksDto getTasksByPerformerId(Long performerId, Integer page, String sort) {
        page = page < 0 ? 0 : page;
        PageRequest pageRequest = createPageRequest(page, sort);
        User user = userService.getUser(performerId);
        return taskMapper.toListDto(taskRepository.findAllByPerformer(user, pageRequest));
    }

    private PageRequest createPageRequest(Integer page, String sort) {
        sort = sort == null ? "asc" : sort;
        Sort sortType = switch (sort) {
            case "asc" -> Sort.by("timestamp").ascending();
            case "desc" -> Sort.by("timestamp").descending();
            default -> Sort.unsorted();
        };
        return PageRequest.of(page, PAGE_SIZE, sortType);
    }

    @Override
    public TaskDto updateTitle(Long id, String title) {
        Task task = getTask(id);
        title = JsonPath.parse(title).read("$.title");
        task.setTitle(title);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateDescription(Long id, String description) {
        Task task = getTask(id);
        description = JsonPath.parse(description).read("$.description");
        task.setDescription(description);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateStatus(Long id, String status) {
        Task task = getTask(id);
        status = JsonPath.parse(status).read("$.status");
        task.setStatus(TaskStatus.valueOf(status));
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updatePriority(Long id, String priority) {
        Task task = getTask(id);
        priority = JsonPath.parse(priority).read("$.priority");
        task.setPriority(TaskPriority.valueOf(priority));
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updatePerformer(Long id, String performerId) {
        Task task = getTask(id);
        performerId = JsonPath.parse(performerId).read("$.performerId");
        User user = userService.getUser(Long.parseLong(performerId));
        task.setPerformer(user);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.delete(getTask(id));
    }
}
