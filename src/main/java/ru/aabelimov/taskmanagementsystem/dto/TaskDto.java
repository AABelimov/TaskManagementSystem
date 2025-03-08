package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.aabelimov.taskmanagementsystem.entity.TaskPriority;
import ru.aabelimov.taskmanagementsystem.entity.TaskStatus;

@Schema(name = "Задача")
public record TaskDto(
        @Schema(description = "id задачи")
        Long id,
        @Schema(description = "Заголовок задачи")
        String title,
        @Schema(description = "Описание задачи")
        String description,
        @Schema(description = "Статус задачи")
        TaskStatus status,
        @Schema(description = "Приоритет задачи")
        TaskPriority priority,
        @Schema(description = "Временная метка создания задачи")
        Long timestamp,
        @Schema(description = "Автор")
        UserDto author,
        @Schema(description = "Исполнитель")
        UserDto performer
) {

}
