package ru.practicum.shareit.utils;

import org.springframework.stereotype.Service;

@Service
public class Validator {
    public  void validateNotNull(Object obj, String name) {
        if (obj == null) {
            throw new ValidationException("Property '" + name + "' must exist");
        }
    }

    public void validateNotEmpty(String str, String name) {
        validateNotNull(str, name);

        if (str.isBlank()) {
            throw new ValidationException("String cannot be empty, property: " + name);
        }
    }

    public void validateEmail(String email) {
        validateNotEmpty(email, "email");

        if (!email.contains("@")) {
            throw new ValidationException("Invalid email");
        }
    }
}
