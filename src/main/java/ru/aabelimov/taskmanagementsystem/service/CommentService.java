package ru.aabelimov.taskmanagementsystem.service;

import org.springframework.security.core.Authentication;
import ru.aabelimov.taskmanagementsystem.dto.CommentDto;
import ru.aabelimov.taskmanagementsystem.dto.CommentsDto;

public interface CommentService {

    CommentDto createComment(Long taskId, String comment, Authentication authentication);

    CommentsDto getCommentsByTaskId(Long taskId, Integer page, String sort);
}
