package ru.aabelimov.taskmanagementsystem.service;

import ru.aabelimov.taskmanagementsystem.dto.JwtAuthenticationResponse;
import ru.aabelimov.taskmanagementsystem.dto.SignInDto;
import ru.aabelimov.taskmanagementsystem.dto.SignUpDto;

public interface AuthenticationService {

    JwtAuthenticationResponse signUp(SignUpDto signUpDto);

    JwtAuthenticationResponse signIn(SignInDto signInDto);
}
