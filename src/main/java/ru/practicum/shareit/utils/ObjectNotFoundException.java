package ru.practicum.shareit.utils;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(int id, String itemType) {
        super("Не найден объект типа '" + itemType + "', id=" + id);
    }
}

