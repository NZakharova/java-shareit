package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable int id) {
        log.info("Запрос пользователя " + id);
        return service.get(id);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        log.info("Запрос пользователей");
        return service.getAll();
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("Создание пользователя: " + user);
        var id = service.add(user);
        return service.get(id);
    }

    @PatchMapping("/users/{id}")
    public UserDto updateUser(@PathVariable int id, @RequestBody UserDto user) {
        log.info("Обновление пользователя " + id + ": " + user);
        service.update(user.toBuilder().id(id).build());
        return service.get(id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Удаление пользователя " + id);
        service.delete(id);
    }
}
