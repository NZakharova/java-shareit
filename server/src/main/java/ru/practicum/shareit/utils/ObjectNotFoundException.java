package ru.practicum.shareit.utils;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(int id, String itemType) {
        super("Object of type '" + itemType + "' is not found, id=" + id);
    }
}

