package ru.aabelimov.taskmanagementsystem.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.aabelimov.taskmanagementsystem.entity.Comment;
import ru.aabelimov.taskmanagementsystem.entity.Task;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByTask(Task task, PageRequest pageRequest);
}
