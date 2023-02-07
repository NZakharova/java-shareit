package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    int create(int bookerId, CreateBookingRequest dto);

    BookingDto get(int bookerId, int id);

    BookingDto get(int id);

    List<BookingDto> getAll(int bookerId, BookingSearchKind searchKind, Pageable pageable);

    List<BookingDto> getAllOwned(int ownerId, BookingSearchKind searchKind, Pageable pageable);

    void setApproved(int bookerId, int bookingId, boolean approved);
}
