package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.Validator;

@Service
@RequiredArgsConstructor
public class ItemValidator {
    private final Validator validator;

    public void validate(Item item) {
        validator.validateNotEmpty(item.getName(), "name");
        validator.validateNotEmpty(item.getDescription(), "description");
    }
}
