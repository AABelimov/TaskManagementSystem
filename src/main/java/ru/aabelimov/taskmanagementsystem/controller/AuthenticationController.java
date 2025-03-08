package ru.aabelimov.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aabelimov.taskmanagementsystem.dto.JwtAuthenticationResponse;
import ru.aabelimov.taskmanagementsystem.dto.SignInDto;
import ru.aabelimov.taskmanagementsystem.dto.SignUpDto;
import ru.aabelimov.taskmanagementsystem.service.AuthenticationService;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Регистрация пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Пользователь зарегистрирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtAuthenticationResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Плохой запрос", content = @Content)
            },
            tags = "Регистрация"
    )
    @PostMapping("sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody SignUpDto signUpDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signUp(signUpDto));
    }

    @Operation(
            summary = "Авторизация пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Пользователь авторизован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtAuthenticationResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Плохой запрос", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = @Content)
            },
            tags = "Авторизация"
    )
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInDto signInDto) {
        return ResponseEntity.ok(authenticationService.signIn(signInDto));
    }
}
