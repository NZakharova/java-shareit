package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.IdGenerator;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final IdGenerator idGenerator;

    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Item>> itemsByUser = new LinkedHashMap<>();

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
        var list = itemsByUser.get(userId);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public int add(Item item) {
        int id = idGenerator.getNextId();
        item.setId(id);
        items.put(id, item);

        itemsByUser.computeIfAbsent(item.getUserId(), i -> new ArrayList<>()).add(item);

        return id;
    }

    @Override
    public void delete(int id) {
        var removed = items.remove(id);
        if (removed != null) {
            itemsByUser.get(removed.getUserId()).remove(removed);
        }
    }

    @Override
    public void updateName(int id, String name) {
        var item = find(id);
        item.setName(name);
    }

    @Override
    public void updateDescription(int id, String description) {
        var item = find(id);
        item.setDescription(description);
    }

    @Override
    public void updateAvailable(int id, boolean available) {
        var item = find(id);
        item.setAvailable(available);
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
