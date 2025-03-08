package ru.aabelimov.taskmanagementsystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            IllegalEmailException.class
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            TaskNotFoundException.class
    })
    public ResponseEntity<?> handleNotFound(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({
            BadCredentialsException.class,
    })
    public ResponseEntity<?> handleBadCredentialsException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверно указан логин или пароль");
    }
}
