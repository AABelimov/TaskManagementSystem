package ru.aabelimov.taskmanagementsystem.exception;

public class IllegalEmailException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Не валидный адрес электронной почты";
    }
}
