package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.aabelimov.taskmanagementsystem.entity.UserRole;

@Schema(name = "Пользователь")
public record UserDto(
        @Schema(description = "id пользователя")
        Long id,
        @Schema(description = "Логин (почта)")
        String username,
        @Schema(description = "Роль пользователя")
        UserRole role
) {
}
