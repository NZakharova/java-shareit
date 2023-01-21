package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.utils.ValidationException;

import java.time.LocalDateTime;

@Service
public class BookingValidator {
    public void validate(CreateBookingRequest dto) {
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало не может быть раньше текущего времени");
        }

        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new ValidationException("Окончание не может быть раньше начала");
        }
    }
}
