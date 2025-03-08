package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JWT токен")
public record JwtAuthenticationResponse(
        @Schema(description = "Токен")
        String token
) {
}
