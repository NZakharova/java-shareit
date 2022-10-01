package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.Validator;

@Service
public class UserValidator {
    private final Validator validator;

    public UserValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(User user) {
        validator.validateNotEmpty(user.getName(), "name");
        validator.validateEmail(user.getEmail());
    }
}
