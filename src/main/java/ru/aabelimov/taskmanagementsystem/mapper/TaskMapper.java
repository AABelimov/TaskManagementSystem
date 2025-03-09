package ru.aabelimov.taskmanagementsystem.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.aabelimov.taskmanagementsystem.dto.CreateTaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TasksDto;
import ru.aabelimov.taskmanagementsystem.entity.Task;
import ru.aabelimov.taskmanagementsystem.entity.TaskStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserMapper userMapper;

    public Task toEntity(CreateTaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(dto.priority());
        task.setTimestamp(System.currentTimeMillis());
        return task;
    }

    public TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getTimestamp(),
                userMapper.toDto(task.getAuthor()),
                userMapper.toDto(task.getPerformer())
        );
    }

    public TasksDto toListDto(List<Task> tasks) {
        return new TasksDto(
                tasks.size(),
                tasks.stream().map(this::toDto).toList()
        );
    }
}
