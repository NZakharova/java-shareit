package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.utils.DateUtils;
import ru.practicum.shareit.utils.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BookingValidatorTests {
    @Autowired
    BookingValidator validator;

    @Test
    void testThrowsForInvalidStart() {
        var dto = CreateBookingRequest.builder()
                .start(DateUtils.now().minusMinutes(1))
                .build();

        assertThrows(ValidationException.class, () -> validator.validate(dto));
    }

    @Test
    void testThrowsForInvalidEnd() {
        var dto = CreateBookingRequest.builder()
                .start(DateUtils.now().plusMinutes(10))
                .end(DateUtils.now().plusMinutes(5))
                .build();

        assertThrows(ValidationException.class, () -> validator.validate(dto));
    }
}
