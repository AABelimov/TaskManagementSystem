package ru.aabelimov.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.aabelimov.taskmanagementsystem.dto.SignUpDto;
import ru.aabelimov.taskmanagementsystem.entity.User;
import ru.aabelimov.taskmanagementsystem.exception.UserNotFoundException;
import ru.aabelimov.taskmanagementsystem.mapper.UserMapper;
import ru.aabelimov.taskmanagementsystem.repository.UserRepository;
import ru.aabelimov.taskmanagementsystem.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceDefaultImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User with username %s not found!".formatted(username))
        );
    }

    @Override
    public User createUser(SignUpDto signUpDto) {
        return userRepository.save(userMapper.toEntity(signUpDto));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

}
