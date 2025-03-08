package ru.aabelimov.taskmanagementsystem.mapper;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.aabelimov.taskmanagementsystem.dto.CommentDto;
import ru.aabelimov.taskmanagementsystem.dto.CommentsDto;
import ru.aabelimov.taskmanagementsystem.entity.Comment;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public Comment toEntity(String comment) {
        Comment commentEntity = new Comment();
        comment = JsonPath.parse(comment).read("$.comment");
        commentEntity.setComment(comment);
        commentEntity.setTimestamp(System.currentTimeMillis());
        return commentEntity;
    }

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getComment(),
                comment.getTimestamp(),
                userMapper.toDto(comment.getAuthor())
        );
    }

    public CommentsDto toListDto(List<Comment> comments) {
        return new CommentsDto(
                comments.size(),
                comments.stream().map(this::toDto).toList()
        );
    }
}
