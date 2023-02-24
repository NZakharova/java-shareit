package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.ValidationException;
import ru.practicum.shareit.utils.Validator;

@Service
@RequiredArgsConstructor
public class UserDtoValidator {
    private final Validator validator;

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
            throw new ValidationException("At least one field is required for update");
        }
    }
}
