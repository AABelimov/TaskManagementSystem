package ru.aabelimov.taskmanagementsystem.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private final String username;

    public UserAlreadyExistsException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return "Пользователь %s уже зарегистрирован!".formatted(username);
    }
}
