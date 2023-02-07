package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Create;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable int id) {
        return client.getUser(id);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers() {
        log.info("Запрос пользователей");
        return client.getUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto user) {
        log.info("Создание пользователя: " + user);
        return client.createUser(user);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable int id, @RequestBody UserDto user) {
        log.info("Обновление пользователя " + id + ": " + user);
        return client.updateUser(id, user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        log.info("Удаление пользователя " + id);
        return client.deleteUser(id);
    }
}
