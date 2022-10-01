package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.ValidationException;
import ru.practicum.shareit.utils.Validator;

@Service
public class ItemDtoValidator {
    private final Validator validator;

    public ItemDtoValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateForUpdate(ItemDto item) {
        int fields = 0;
        if (item.getName() != null) {
            validator.validateNotEmpty(item.getName(), "name");
            fields++;
        }

        if (item.getAvailable() != null) {
            validator.validateNotNull(item.getAvailable(), "available");
            fields++;
        }

        if (item.getUserId() != null) {
            validator.validateNotNull(item.getUserId(), "userId");
            fields++;
        }

        if (item.getDescription() != null) {
            validator.validateNotEmpty(item.getDescription(), "description");
            fields++;
        }

        if (fields == 0) {
            throw new ValidationException("Для обновления требуется хотя бы одно поле");
        }
    }
}
