package ru.practicum.shareit.utils;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(int id) {
        super("Не найден объект, id=" + id);
    }
}

