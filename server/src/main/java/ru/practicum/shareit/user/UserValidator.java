package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.Validator;

@Service
@RequiredArgsConstructor
public class UserValidator {
    private final Validator validator;

    public void validate(User user) {
        validator.validateNotEmpty(user.getName(), "name");
        validator.validateEmail(user.getEmail());
    }
}
