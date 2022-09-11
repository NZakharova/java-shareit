package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    int add(User user);

    User find(int id);

    Collection<User> findAll();

    void updateName(int id, String name);

    void updateEmail(int id, String email);

    void delete(int id);
}
