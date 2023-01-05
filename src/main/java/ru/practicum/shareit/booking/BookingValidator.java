package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.utils.ValidationException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingValidator {
    private final ItemService itemService;

    public void validate(CreateBookingRequest dto) {
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало не может быть раньше текущего времени");
        }

        if (dto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Окончание не может быть раньше текущего времени");
        }

        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new ValidationException("Окончание не может быть раньше начала");
        }

        var item = itemService.get(dto.getItemId());
        if (!item.getAvailable()) {
            throw new ItemUnavailableException();
        }
    }
}
