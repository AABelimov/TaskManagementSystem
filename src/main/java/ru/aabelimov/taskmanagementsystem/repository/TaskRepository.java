package ru.aabelimov.taskmanagementsystem.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.aabelimov.taskmanagementsystem.entity.Task;
import ru.aabelimov.taskmanagementsystem.entity.User;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAuthor(User author, PageRequest pageRequest);

    List<Task> findAllByPerformer(User performer, PageRequest pageRequest);
}
