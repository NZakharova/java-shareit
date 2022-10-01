package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item find(int id);

    Collection<Item> findAll();

    Collection<Item> findAll(int userId);

    int add(Item item);

    void delete(int id);

    void updateName(int id, String name);

    void updateDescription(int id, String description);

    void updateAvailable(int id, boolean available);

    Collection<Item> search(String text);
}
