package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.Create;

import java.util.List;

@RestController
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable int id) {
        return service.find(id);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return service.findAll();
    }

    @PostMapping("/users")
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto user) {
        var id = service.add(user);
        return service.find(id);
    }

    @PatchMapping("/users/{id}")
    public UserDto updateUser(@PathVariable int id, @RequestBody UserDto user) {
        service.update(user.toBuilder().id(id).build());
        return service.find(id);
    }

    @DeleteMapping("/users/{id}")
    public void updateUser(@PathVariable int id) {
        service.delete(id);
    }
}
