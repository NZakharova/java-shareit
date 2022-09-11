package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.IdGenerator;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final IdGenerator idGenerator;

    private final Map<Integer, Item> items = new HashMap<>();

    public InMemoryItemStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Item find(int id) {
        var item = items.get(id);
        if (item == null) {
            throw new ObjectNotFoundException(id);
        }

        return item;
    }

    @Override
    public Collection<Item> findAll() {
        return items.values().stream().filter(Item::isAvailable).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findAll(int userId) {
        return items.values().stream().filter(i -> i.getUserId() == userId).collect(Collectors.toList());
    }

    @Override
    public int add(Item item) {
        var forAdd = item.toBuilder().id(idGenerator.getNextId()).build();
        items.put(forAdd.getId(), forAdd);
        return forAdd.getId();
    }

    @Override
    public void delete(int id) {
        items.remove(id);
    }

    @Override
    public void updateName(int id, String name) {
        var item = find(id);
        var updated = item.toBuilder().name(name).build();
        items.replace(id, updated);
    }

    @Override
    public void updateDescription(int id, String description) {
        var item = find(id);
        var updated = item.toBuilder().description(description).build();
        items.replace(id, updated);
    }

    @Override
    public void updateAvailable(int id, boolean available) {
        var item = find(id);
        var updated = item.toBuilder().available(available).build();
        items.replace(id, updated);
    }

    @Override
    public Collection<Item> search(String text) {
        return findAll()
                .stream()
                .filter(x -> containsIgnoreCase(x.getName(), text)
                        || containsIgnoreCase(x.getDescription(), text))
                .collect(Collectors.toList());
    }

    private static boolean containsIgnoreCase(String source, String str) {
        return Pattern.compile(Pattern.quote(str), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(source).find();
    }
}
