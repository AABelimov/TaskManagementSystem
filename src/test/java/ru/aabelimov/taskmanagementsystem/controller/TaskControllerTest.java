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
import ru.aabelimov.taskmanagementsystem.dto.CreateTaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TaskDto;
import ru.aabelimov.taskmanagementsystem.dto.TasksDto;
import ru.aabelimov.taskmanagementsystem.entity.TaskPriority;
import ru.aabelimov.taskmanagementsystem.entity.TaskStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:prepare_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:cleanup_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TaskControllerTest {

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
    void testCreateTask_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        // Валидные данные
        CreateTaskDto task = new CreateTaskDto("Новая задача", "Описание", TaskPriority.HIGH, 102L);
        HttpEntity<CreateTaskDto> request = new HttpEntity<>(task, headers);

        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks", HttpMethod.POST, request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().title()).isEqualTo("Новая задача");
        assertThat(response.getBody().description()).isEqualTo("Описание");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Невалидный id исполнителя
        task = new CreateTaskDto("Новая задача", "Описание", TaskPriority.HIGH, 100001L);
        request = new HttpEntity<>(task, headers);

        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks", HttpMethod.POST, request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Пользователь с id = 100001 не найден!");
    }

    @Test
    void testCreateTask_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        CreateTaskDto task = new CreateTaskDto("Новая задача", "Описание", TaskPriority.HIGH, 102L);
        HttpEntity<CreateTaskDto> request = new HttpEntity<>(task, headers);

        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks", HttpMethod.POST, request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testGetTask_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Валидный запрос
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101", HttpMethod.GET, request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789L);
        assertThat(response.getBody().author().username()).isEqualTo("admin@admin.ru");
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Запрос задачи с несуществующим id
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001", HttpMethod.GET, request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testGetTask_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Запрос своей задачи
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101", HttpMethod.GET, request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789L);
        assertThat(response.getBody().author().username()).isEqualTo("admin@admin.ru");
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Запрос не своей задачи
        response = restTemplate.exchange("/tasks/102", HttpMethod.GET, request, TaskDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testGetTasksByAuthor_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        //Валидные данные, asc sort
        ResponseEntity<TasksDto> response = restTemplate.exchange("/tasks/author/101?page=0", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(2);
        assertThat(response.getBody().comments().getFirst().timestamp() < response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        //Валидные данные, desc sort
        response = restTemplate.exchange("/tasks/author/101?page=0&sort=desc", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(2);
        assertThat(response.getBody().comments().getFirst().timestamp() > response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        // Запрос с несуществующим id автора
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/author/100001?page=0", HttpMethod.GET, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Пользователь с id = 100001 не найден!");
    }

    @Test
    void testGetTasksByAuthor_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<TasksDto> response = restTemplate.exchange("/tasks/author/101?page=0", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testGetTasksByPerformer_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        //Валидные данные, asc sort
        ResponseEntity<TasksDto> response = restTemplate.exchange("/tasks/performer/112?page=0", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(2);
        assertThat(response.getBody().comments().getFirst().timestamp() < response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        //Валидные данные, desc sort
        response = restTemplate.exchange("/tasks/performer/112?page=0&sort=desc", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(2);
        assertThat(response.getBody().comments().getFirst().timestamp() > response.getBody().comments().getLast().timestamp()).isEqualTo(true);

        // Запрос с несуществующим id автора
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/performer/100001?page=0", HttpMethod.GET, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Пользователь с id = 100001 не найден!");
    }

    @Test
    void testGetTasksByPerformer_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Получить свои задачи
        ResponseEntity<TasksDto> response = restTemplate.exchange("/tasks/performer/102?page=0", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().count()).isEqualTo(1);

        // Получить чужие задачи
        response = restTemplate.exchange("/tasks/performer/112?page=0", HttpMethod.GET, request, TasksDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testUpdateTitle_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>("{\"title\": \"newTitle\"}", headers);

        // Валидные данные
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-title", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("newTitle");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Неверный id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001/update-title", HttpMethod.PATCH,request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testUpdateTitle_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>("{\"title\": \"newTitle\"}", headers);

        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-title", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testUpdateDescription_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>("{\"description\": \"newDescription\"}", headers);

        // Валидные данные
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-description", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("newDescription");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Неверный id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001/update-description", HttpMethod.PATCH,request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testUpdateDescription_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>("{\"description\": \"newDescription\"}", headers);

        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-description", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testUpdateStatus_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>("{\"status\": \"COMPLETED\"}", headers);

        // Валидные данные
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-status", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Неверный id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001/update-status", HttpMethod.PATCH,request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testUpdateStatus_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>("{\"status\": \"COMPLETED\"}", headers);

        // Обновление статуса своей задачи
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-status", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Попытка обновления статуса не своей задачи
        ResponseEntity<TaskDto> badResponse = restTemplate.exchange("/tasks/102/update-status", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testUpdatePriority_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>("{\"priority\": \"LOW\"}", headers);

        // Валидные данные
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-priority", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.LOW);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user@user.ru");

        // Неверный id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001/update-priority", HttpMethod.PATCH,request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testUpdatePriority_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>("{\"priority\": \"LOW\"}", headers);

        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-priority", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testUpdatePerformer_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>("{\"performerId\": \"112\"}", headers);

        // Валидные данные
        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-performer", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(101L);
        assertThat(response.getBody().title()).isEqualTo("task title");
        assertThat(response.getBody().description()).isEqualTo("task description");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getBody().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.getBody().timestamp()).isEqualTo(123456789);
        assertThat(response.getBody().author().id()).isEqualTo(101L);
        assertThat(response.getBody().performer().username()).isEqualTo("user2@user.ru");

        // Неверный id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001/update-performer", HttpMethod.PATCH,request, String.class);
        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testUpdatePerformer_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>("{\"performerId\": \"112\"}", headers);

        ResponseEntity<TaskDto> response = restTemplate.exchange("/tasks/101/update-performer", HttpMethod.PATCH,request, TaskDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testDeleteTask_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<String> request = new HttpEntity<>( headers);

        // Валидные данные
        ResponseEntity<Void> response = restTemplate.exchange("/tasks/101", HttpMethod.DELETE, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Неверный id задачи
        ResponseEntity<String> badResponse = restTemplate.exchange("/tasks/100001", HttpMethod.DELETE, request, String.class);

        assertThat(badResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(badResponse.getBody()).isEqualTo("Задача с id = 100001 не найдена!");
    }

    @Test
    void testDeleteTask_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);

        HttpEntity<String> request = new HttpEntity<>( headers);

        ResponseEntity<Void> response = restTemplate.exchange("/tasks/101", HttpMethod.DELETE, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
