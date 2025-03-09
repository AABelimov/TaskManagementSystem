package ru.aabelimov.taskmanagementsystem.exception;

public class CommentNotFoundException extends RuntimeException {

    private final Long id;

    public CommentNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Комментарий с id = %d не найден!".formatted(id);
    }
}
