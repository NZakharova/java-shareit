package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.utils.DuplicateObjectException;
import ru.practicum.shareit.utils.IdGenerator;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final IdGenerator idGenerator;
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();

    public InMemoryUserStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public int add(User user) {
        if (emails.containsKey(user.getEmail())) {
            throw new DuplicateObjectException("email");
        }

        var id = idGenerator.getNextId();
        var userWithId = user.toBuilder().id(id).build();
        users.put(id, userWithId);
        emails.put(userWithId.getEmail(), userWithId);
        return id;
    }

    @Override
    public User find(int id) {
        var user = users.get(id);
        if (user == null) {
            throw new ObjectNotFoundException(id);
        }

        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void updateName(int id, String name) {
        var existing = find(id);
        var updated = existing.toBuilder().name(name).build();
        users.replace(id, updated);
    }

    @Override
    public void updateEmail(int id, String email) {
        var existing = find(id);

        if (emails.containsKey(email)) {
            throw new DuplicateObjectException("email");
        }

        var updated = existing.toBuilder().email(email).build();
        users.replace(id, updated);
        emails.remove(existing.getEmail());
        emails.put(email, updated);
    }

    @Override
    public void delete(int id) {
        var existing = users.get(id);
        if (existing == null) {
            throw new ObjectNotFoundException(id);
        }

        emails.remove(existing.getEmail());
        users.remove(id);
    }
}
