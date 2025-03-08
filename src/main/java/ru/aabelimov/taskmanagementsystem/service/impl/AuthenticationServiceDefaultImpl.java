package ru.aabelimov.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.aabelimov.taskmanagementsystem.config.JwtTokenUtility;
import ru.aabelimov.taskmanagementsystem.dto.JwtAuthenticationResponse;
import ru.aabelimov.taskmanagementsystem.dto.SignInDto;
import ru.aabelimov.taskmanagementsystem.dto.SignUpDto;
import ru.aabelimov.taskmanagementsystem.entity.User;
import ru.aabelimov.taskmanagementsystem.exception.IllegalEmailException;
import ru.aabelimov.taskmanagementsystem.exception.UserAlreadyExistsException;
import ru.aabelimov.taskmanagementsystem.service.AuthenticationService;
import ru.aabelimov.taskmanagementsystem.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceDefaultImpl implements AuthenticationService {

    private static final Pattern PATTERN_EMAIL = Pattern.compile("^[\\w-.]+@[\\w-]+\\.+[\\w-]{2,}$");

    private final UserService userService;
    private final JwtTokenUtility jwtTokenUtility;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtAuthenticationResponse signUp(SignUpDto signUpDto) {
        if (userService.existsByUsername(signUpDto.username())) {
            throw new UserAlreadyExistsException(signUpDto.username());
        }

        if (emailIsCorrect(signUpDto.username())) {
            User user = userService.createUser(signUpDto.setPassword(passwordEncoder.encode(signUpDto.password())));
            String token = jwtTokenUtility.generateToken(user);

            return new JwtAuthenticationResponse(token);
        }
        throw new IllegalEmailException();
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInDto signInDto) {
        if (emailIsCorrect(signInDto.username())) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDto.username(), signInDto.password()));
            UserDetails user = userService.loadUserByUsername(signInDto.username());
            String token = jwtTokenUtility.generateToken(user);
            return new JwtAuthenticationResponse(token);
        }
        throw new IllegalEmailException();
    }

    private boolean emailIsCorrect(String email) {
        Matcher emailMatcher = PATTERN_EMAIL.matcher(email);
        return emailMatcher.matches();
    }
}
