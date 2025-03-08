package ru.aabelimov.taskmanagementsystem.mapper;

import org.springframework.stereotype.Component;
import ru.aabelimov.taskmanagementsystem.dto.SignUpDto;
import ru.aabelimov.taskmanagementsystem.dto.UserDto;
import ru.aabelimov.taskmanagementsystem.entity.User;
import ru.aabelimov.taskmanagementsystem.entity.UserRole;

@Component
public class UserMapper {

    public User toEntity(SignUpDto signUpDto) {
        User user = new User();
        user.setUsername(signUpDto.username());
        user.setPassword(signUpDto.password());
        user.setRole(UserRole.ROLE_USER);
        return user;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }
}
