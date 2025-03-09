package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Задачи")
public record TasksDto(
        @Schema(description = "Количество задач")
        Integer count,
        @Schema(description = "Задачи")
        List<TaskDto> comments
) {
}
