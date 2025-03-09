package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Комментарий")
public record CommentDto(
        @Schema(description = "id комментария")
        Long id,
        @Schema(description = "Текст комментария")
        String comment,
        @Schema(description = "Временная метка создания комментария")
        Long timestamp,
        @Schema(description = "Автор комментария")
        UserDto author
) {
}
