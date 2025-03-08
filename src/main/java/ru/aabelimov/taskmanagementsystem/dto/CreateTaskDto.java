package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.aabelimov.taskmanagementsystem.entity.TaskPriority;

@Schema(name = "Данные для создания задачи")
public record CreateTaskDto(
        @Schema(description = "Заголовок задачи")
        String title,
        @Schema(description = "Описание задачи")
        String description,
        @Schema(description = "Приоритет задачи")
        TaskPriority priority,
        @Schema(description = "id исполнителя")
        Long performerId
) {
}
