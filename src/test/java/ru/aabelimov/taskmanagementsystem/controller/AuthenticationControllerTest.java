package ru.aabelimov.taskmanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.aabelimov.taskmanagementsystem.dto.JwtAuthenticationResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:prepare_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:cleanup_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testSignUp(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Валидные данные
        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "newuser@user.ru", "newUser");
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<JwtAuthenticationResponse> response = restTemplate.exchange("/auth/sign-up", HttpMethod.POST, request, JwtAuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotEmpty();

        // Попытка регистрации с почтой, с которой ранее уже зарегистрировались
        requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "user@user.ru", "user");
        request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> badResponse = restTemplate.exchange("/auth/sign-up", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(badResponse.getBody()).isEqualTo("Пользователь user@user.ru уже зарегистрирован!");

        // Попытка регистрации с невалидной почтой
        requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "user", "user");
        request = new HttpEntity<>(requestBody, headers);
        badResponse = restTemplate.exchange("/auth/sign-up", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(badResponse.getBody()).isEqualTo("Не валидный адрес электронной почты");
    }

    @Test
    void testSignIn() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Валидные данные
        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "admin@admin.ru", "admin");
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/auth/sign-in", request, JwtAuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotEmpty();

        // Попытка войти с невалидной почтой
        requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "admin", "admin");
        request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> badResponse = restTemplate.exchange("/auth/sign-in", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(badResponse.getBody()).isEqualTo("Не валидный адрес электронной почты");

        // Попытка войти с неверным логином или паролем
        requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "admin@admin.ru", "neAdmin");
        request = new HttpEntity<>(requestBody, headers);
        badResponse = restTemplate.exchange("/auth/sign-in", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(badResponse.getBody()).isEqualTo("Неверно указан логин или пароль");

        requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "neadmin@admin.ru", "admin");
        request = new HttpEntity<>(requestBody, headers);
        badResponse = restTemplate.exchange("/auth/sign-in", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(badResponse.getBody()).isEqualTo("Неверно указан логин или пароль");
    }

}