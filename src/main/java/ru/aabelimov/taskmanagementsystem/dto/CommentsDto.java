package ru.aabelimov.taskmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Комментарии")
public record CommentsDto(
        @Schema(description = "Количество комментариев")
        Integer count,
        @Schema(description = "Комментарии")
        List<CommentDto> comments
) {
}
