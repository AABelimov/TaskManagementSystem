package ru.aabelimov.taskmanagementsystem.exception;

public class UserNotFoundException extends RuntimeException {

    private final Long id;

    public UserNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Пользователь с id = %d не найден!".formatted(id);
    }
}
