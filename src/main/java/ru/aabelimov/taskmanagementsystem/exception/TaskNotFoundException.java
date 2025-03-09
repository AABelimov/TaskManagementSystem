package ru.aabelimov.taskmanagementsystem.exception;

public class TaskNotFoundException extends RuntimeException {

    private final Long id;

    public TaskNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Задача с id = %d не найдена!".formatted(id);
    }
}
