package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Данные для регистрации")
public record SignUpDto(
        @Schema(description = "Логин (почта)")
        String username,
        @Schema(description = "Пароль")
        String password
) {

    public SignUpDto setPassword(String password) {
        return new SignUpDto(username, password);
    }
}
