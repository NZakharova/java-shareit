package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.ObjectNotFoundException;
import ru.practicum.shareit.utils.ValidationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BookingServiceTests {
    @Autowired
    BookingService bookingService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    @BeforeEach
    void setup() {
        Mockito.when(userService.get(1)).thenReturn(UserDto.builder().id(1).build());
        Mockito.when(userService.get(2)).thenReturn(UserDto.builder().id(2).build());
        Mockito.when(userService.get(3)).thenReturn(UserDto.builder().id(3).build());

        Mockito.when(itemService.get(1)).thenReturn(ItemDto.builder().id(1).available(true).userId(1).build());

        var startDate = LocalDateTime.now().plusHours(1);
        var endDate = startDate.plusHours(1);
        Mockito.when(bookingRepository.findById(1)).thenReturn(Optional.of(new Booking(1, 1, BookingStatus.WAITING, 2, startDate, endDate)));
        Mockito.when(bookingRepository.findById(2)).thenReturn(Optional.of(new Booking(2, 1, BookingStatus.REJECTED, 2, startDate, endDate)));
    }

    @Test
    void testCannotAddForOwnItem() {
        var startDate = LocalDateTime.now().plusHours(1);
        var endDate = startDate.plusHours(1);

        var request = new CreateBookingRequest(1, startDate, endDate);
        assertThrows(ObjectNotFoundException.class, () -> bookingService.create(1, request));
    }

    @Test
    void testCanCreateBooking() {
        var startDate = LocalDateTime.now().plusHours(1);
        var endDate = startDate.plusHours(1);

        bookingService.create(3, new CreateBookingRequest(1, startDate, endDate));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(Mockito.any());
    }

    @Test
    void testCannotSeeForeignBooking() {
        assertThrows(ObjectNotFoundException.class, () -> bookingService.get(3, 1));
    }

    @Test
    void testCanSeeOwnBooking() {
        assertDoesNotThrow(() -> bookingService.get(1, 1));
        assertDoesNotThrow(() -> bookingService.get(2, 1));
    }

    @Test
    void testApproval() {
        assertDoesNotThrow(() -> bookingService.setApproved(1, 1, true));
        assertDoesNotThrow(() -> bookingService.setApproved(1, 1, false));
    }

    @Test
    void testCannotApproveAlreadyApproved() {
        assertThrows(ValidationException.class, () -> bookingService.setApproved(1, 2, true));
        assertThrows(ValidationException.class, () -> bookingService.setApproved(1, 2, false));
    }
}
