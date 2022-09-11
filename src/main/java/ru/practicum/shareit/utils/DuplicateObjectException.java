package ru.practicum.shareit.utils;

public class DuplicateObjectException extends RuntimeException {
    public DuplicateObjectException(String field) {
        super("Сущность с полем '" + field + "' уже сущеуствует");
    }
}
