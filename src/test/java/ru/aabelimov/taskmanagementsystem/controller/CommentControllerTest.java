package ru.aabelimov.taskmanagementsystem.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.aabelimov.taskmanagementsystem.dto.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:prepare_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:cleanup_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CommentControllerTest {

    @Autowired
    private TaskController taskController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void beforeEach() {
        adminToken = authenticate("admin@admin.ru", "admin");
        userToken = authenticate("user@user.ru", "user");
    }

    private String authenticate(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/sign-in", request, String.class);
        return JsonPath.parse(response.getBody()).read("$.token");
    }

    @Test
    void contextLoad() {
        assertNotNull(taskController);
    }

    @Test
    void testCreateComment_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        // Валидные данные
        HttpEntity<String> request = new HttpEntity<>("{\"comment\":\"comment\"}", headers);
        ResponseEntity<CommentDto> response = restTemplate.exchange("/comments/task/101", HttpMethod.POST, request, CommentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().comment()).isEqualTo("comment");
        assertThat(response.getBody().author().id()).isEqualTo(101L);

        // Невалидный id задачи
        request = new HttpEntity<>("{\"comment\":\"comment\"}", headers);
        ResponseEntity<String> badResponse = restTemplate.exchange("/comments/task/100001", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testCreateComment_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        // Добавление комментария к своей задаче
        HttpEntity<String> request = new HttpEntity<>("{\"comment\":\"comment\"}", headers);
        ResponseEntity<CommentDto> response = restTemplate.exchange("/comments/task/101", HttpMethod.POST, request, CommentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().comment()).isEqualTo("comment");
        assertThat(response.getBody().author().id()).isEqualTo(102L);

        // Добавление комментария к чужой задаче
        ResponseEntity<String> badResponse = restTemplate.exchange("/comments/task/102", HttpMethod.POST, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testGetCommentsByTaskId_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        //Валидные данные, asc sort
        ResponseEntity<CommentsDto> response = restTemplate.exchange("/comments/task/101?page=0", HttpMethod.GET, request, CommentsDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(3);
        assertThat(response.getBody().comments().getFirst().timestamp() < response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        //Валидные данные, desc sort
        response = restTemplate.exchange("/comments/task/101?page=0&sort=desc", HttpMethod.GET, request, CommentsDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(3);
        assertThat(response.getBody().comments().getFirst().timestamp() > response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        // Запрос с несуществующим id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/comments/task/100001?page=0", HttpMethod.GET, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testGetCommentsByTaskId_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        //Запрос комментариев к своей задаче
        ResponseEntity<CommentsDto> response = restTemplate.exchange("/comments/task/101?page=0", HttpMethod.GET, request, CommentsDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(3);
        assertThat(response.getBody().comments().getFirst().timestamp() < response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        //Запрос комментариев к чужой задаче
        response = restTemplate.exchange("/comments/task/103?page=0", HttpMethod.GET, request, CommentsDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testUpdateComment() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>("{\"comment\":\"updated comment\"}", headers);

        // Обновление своего коммента
        ResponseEntity<CommentDto> response = restTemplate.exchange("/comments/103", HttpMethod.PATCH, request, CommentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(103L);
        assertThat(response.getBody().comment()).isEqualTo("updated comment");
        assertThat(response.getBody().author().id()).isEqualTo(101L);

        // Обновление чужого коммента
        ResponseEntity<String> badResponse = restTemplate.exchange("/comments/101", HttpMethod.PATCH, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Обновление несуществующего коммента
        badResponse = restTemplate.exchange("/comments/100001", HttpMethod.PATCH, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Комментарий с id = 100001 не найден!");
    }

    @Test
    void testDeleteComment_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Валидные данные
        ResponseEntity<String> response = restTemplate.exchange("/comments/101", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Удаление несуществующего коммента
        response = restTemplate.exchange("/comments/100001", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Комментарий с id = 100001 не найден!");
    }

    @Test
    void testDeleteComment_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Удаление своего комментария
        ResponseEntity<String> response = restTemplate.exchange("/comments/101", HttpMethod.DELETE, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Удаление чужого комментария
        response = restTemplate.exchange("/comments/103", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}