package ru.practicum.shareit.utils;

import org.springframework.stereotype.Service;

@Service
public class Validator {
    public  void validateNotNull(Object obj, String name) {
        if (obj == null) {
            throw new ValidationException("Объект должен присутсвовать, свойство: " + name);
        }
    }

    public void validateNotEmpty(String str, String name) {
        validateNotNull(str, name);

        if (str.isBlank()) {
            throw new ValidationException("Строка не может быть пустой, свойство: " + name);
        }
    }

    public void validateEmail(String email) {
        validateNotEmpty(email, "email");

        if (!email.contains("@")) {
            throw new ValidationException("Некорректный email");
        }
    }
}
