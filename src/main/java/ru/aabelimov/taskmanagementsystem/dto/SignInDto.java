package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Данные для авторизации")
public record SignInDto(
        @Schema(description = "Логин (почта)")
        String username,
        @Schema(description = "Пароль")
        String password
) {
}
