package ru.aabelimov.taskmanagementsystem.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.aabelimov.taskmanagementsystem.dto.SignUpDto;
import ru.aabelimov.taskmanagementsystem.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    User createUser(SignUpDto signUpDto);

    boolean existsByUsername(String username);

    User getUser(Long id);

}
