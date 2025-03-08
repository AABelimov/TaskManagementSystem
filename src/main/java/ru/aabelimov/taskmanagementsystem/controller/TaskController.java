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
import ru.aabelimov.taskmanagementsystem.dto.CreateTaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TasksDto;
import ru.aabelimov.taskmanagementsystem.mapper.TaskMapper;
import ru.aabelimov.taskmanagementsystem.service.TaskService;

@Tag(name = "Задачи")
@RestController
@RequestMapping("tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Operation(
            summary = "Добавить задачу",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Задача добавлена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> createTask(@RequestBody CreateTaskDto dto, Authentication authentication) {
        return ResponseEntity.ok(taskService.createTask(dto, authentication));
    }

    @Operation(
            summary = "Получить задачу",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Задача получена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @taskServiceDefaultImpl.getTask(#id).performer.username == authentication.name")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskMapper.toDto(taskService.getTask(id)));
    }

    @Operation(
            summary = "Получить список задач по id автора",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Список задач получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TasksDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @GetMapping("author/{authorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TasksDto> getTasksByAuthor(@PathVariable Long authorId,
                                                     @RequestParam Integer page,
                                                     @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(taskService.getTasksByAuthorId(authorId, page, sort));
    }

    @Operation(
            summary = "Получить список задач по id исполнителя",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Список задач получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TasksDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @GetMapping("performer/{performerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @userServiceDefaultImpl.getUser(#performerId).username == authentication.name")
    public ResponseEntity<TasksDto> getTasksByPerformer(@PathVariable Long performerId,
                                                        @RequestParam Integer page,
                                                        @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(taskService.getTasksByPerformerId(performerId, page, sort));
    }

    @Operation(
            summary = "Обновить заголовок задачи",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Заголовок обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @PatchMapping("{id}/update-title")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updateTitle(@PathVariable Long id, @RequestBody String title) {
        return ResponseEntity.ok(taskService.updateTitle(id, title));
    }

    @Operation(
            summary = "Обновить описание задачи",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Описание обновлено",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @PatchMapping("{id}/update-description")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updateDescription(@PathVariable Long id, @RequestBody String description) {
        return ResponseEntity.ok(taskService.updateDescription(id, description));
    }

    @Operation(
            summary = "Обновить статус задачи",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Статус обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @PatchMapping("{id}/update-status")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @taskServiceDefaultImpl.getTask(#id).performer.username == authentication.name")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @Operation(
            summary = "Обновить приоритет задачи",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Приоритет обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @PatchMapping("{id}/update-priority")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updatePriority(@PathVariable Long id, @RequestBody String priority) {
        return ResponseEntity.ok(taskService.updatePriority(id, priority));
    }

    @Operation(
            summary = "Обновить исполнителя задачи",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Исполнитель обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @PatchMapping("{id}/update-performer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> updatePerformer(@PathVariable Long id, @RequestBody String performerId) {
        return ResponseEntity.ok(taskService.updatePerformer(id, performerId));
    }

    @Operation(
            summary = "Удалить задачу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача обновлена", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
