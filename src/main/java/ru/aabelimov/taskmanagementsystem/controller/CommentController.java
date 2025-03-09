package ru.aabelimov.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.aabelimov.taskmanagementsystem.dto.CommentDto;
import ru.aabelimov.taskmanagementsystem.dto.CommentsDto;
import ru.aabelimov.taskmanagementsystem.service.CommentService;

@Tag(name = "Комментарии")
@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Добавить комментарий к задаче",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Комментарий добавлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Не хватает прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Задачи с таким id не найдено", content = @Content)
            }
    )
    @PostMapping("task/{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @taskServiceDefaultImpl.getTask(#taskId).performer.username == authentication.name")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long taskId,
                                                    @RequestBody String comment,
                                                    Authentication authentication) {
        return ResponseEntity.ok(commentService.createComment(taskId, comment, authentication));
    }

    @Operation(
            summary = "Получить список комментариев по id задачи",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Список комментариев получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentsDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Не хватает прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Задачи с таким id не найдено", content = @Content)
            }
    )
    @GetMapping("task/{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @taskServiceDefaultImpl.getTask(#taskId).performer.username == authentication.name")
    public ResponseEntity<CommentsDto> getCommentsByTaskId(@PathVariable Long taskId,
                                                           @RequestParam Integer page,
                                                           @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId, page, sort));
    }

    @Operation(
            summary = "Редактировать комментарий",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Комментарий отредактирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Не хватает прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Комментария с таким id не найдено", content = @Content)
            }
    )
    @PatchMapping("{id}")
    @PreAuthorize("@commentServiceDefaultImpl.getComment(#id).author.username == authentication.name")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @RequestBody String comment) {
        return ResponseEntity.ok(commentService.updateComment(id, comment));
    }

    @Operation(
            summary = "Удалить комментарий",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Комментарий удален",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Не хватает прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Комментария с таким id не найдено", content = @Content)
            }
    )
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @commentServiceDefaultImpl.getComment(#id).author.username == authentication.name")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
