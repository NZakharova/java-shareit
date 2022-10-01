package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.ValidationException;
import ru.practicum.shareit.utils.Validator;

@Service
public class UserDtoValidator {
    private final Validator validator;

    public UserDtoValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForUpdate(UserDto dto) {
        int fields = 0;
        if (dto.getName() != null) {
            validator.validateNotEmpty(dto.getName(), "name");
            fields++;
        }

        if (dto.getEmail() != null) {
            validator.validateEmail(dto.getEmail());
            fields++;
        }

        if (fields == 0) {
            throw new ValidationException("Для обновления требуется хотя бы одно поле");
        }
    }
}
