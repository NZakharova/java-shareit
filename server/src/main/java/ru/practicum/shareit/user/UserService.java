package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    int add(UserDto user);

    UserDto get(int id);

    List<UserDto> getAll();

    void update(UserDto dto);

    void delete(int id);
}
