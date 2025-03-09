package ru.aabelimov.taskmanagementsystem.service.impl;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.aabelimov.taskmanagementsystem.dto.CommentDto;
import ru.aabelimov.taskmanagementsystem.dto.CommentsDto;
import ru.aabelimov.taskmanagementsystem.entity.Comment;
import ru.aabelimov.taskmanagementsystem.entity.Task;
import ru.aabelimov.taskmanagementsystem.entity.User;
import ru.aabelimov.taskmanagementsystem.exception.CommentNotFoundException;
import ru.aabelimov.taskmanagementsystem.mapper.CommentMapper;
import ru.aabelimov.taskmanagementsystem.repository.CommentRepository;
import ru.aabelimov.taskmanagementsystem.service.CommentService;
import ru.aabelimov.taskmanagementsystem.service.TaskService;
import ru.aabelimov.taskmanagementsystem.service.UserService;

@Service
@RequiredArgsConstructor
public class CommentServiceDefaultImpl implements CommentService {

    public static final int PAGE_SIZE = 5;

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskService taskService;
    private final UserService userService;

    @Override
    public CommentDto createComment(Long taskId, String comment, Authentication authentication) {
        Task task = taskService.getTask(taskId);
        Comment commentEntity = commentMapper.toEntity(comment);
        commentEntity.setTask(task);
        commentEntity.setAuthor((User) userService.loadUserByUsername(authentication.getName()));
        return commentMapper.toDto(commentRepository.save(commentEntity));
    }

    @Override
    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    public CommentsDto getCommentsByTaskId(Long taskId, Integer page, String sort) {
        Task task = taskService.getTask(taskId);
        PageRequest pageRequest = createPageRequest(page, sort);
        return commentMapper.toListDto(commentRepository.findAllByTask(task, pageRequest));
    }

    @Override
    public CommentDto updateComment(Long id, String comment) {
        Comment commentEntity = getComment(id);
        comment = JsonPath.parse(comment).read("$.comment");
        commentEntity.setComment(comment);
        return commentMapper.toDto(commentRepository.save(commentEntity));
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.delete(getComment(id));
    }

    private PageRequest createPageRequest(Integer page, String sort) {
        sort = sort == null ? "asc" : sort;
        Sort sortType = switch (sort) {
            case "asc" -> Sort.by("timestamp").ascending();
            case "desc" -> Sort.by("timestamp").descending();
            default -> Sort.unsorted();
        };
        return PageRequest.of(page, PAGE_SIZE, sortType);
    }
}
