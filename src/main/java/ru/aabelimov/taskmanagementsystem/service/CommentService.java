package ru.aabelimov.taskmanagementsystem.service;

import org.springframework.security.core.Authentication;
import ru.aabelimov.taskmanagementsystem.dto.CommentDto;
import ru.aabelimov.taskmanagementsystem.dto.CommentsDto;
import ru.aabelimov.taskmanagementsystem.entity.Comment;

public interface CommentService {

    CommentDto createComment(Long taskId, String comment, Authentication authentication);

    Comment getComment(Long id);

    CommentsDto getCommentsByTaskId(Long taskId, Integer page, String sort);

    CommentDto updateComment(Long id, String comment);

    void deleteComment(Long id);
}
