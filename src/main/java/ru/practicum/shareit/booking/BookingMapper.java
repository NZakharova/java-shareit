package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

@Service
@RequiredArgsConstructor
public class BookingMapper {
    public BookingDto toDto(Booking booking, ItemDto item, UserDto booker) {
        return new BookingDto(booking.getId(),
                booking.getBookerId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                item,
                booker
        );
    }

    public static ShortBookingDto toShortDto(Booking booking) {
        return new ShortBookingDto(booking.getId(), booking.getBookerId());
    }

    public Booking toModel(CreateBookingRequest request, Integer requesterId) {
        return new Booking(
                null, request.getItemId(), BookingStatus.WAITING, requesterId, request.getStart(), request.getEnd()
        );
    }
}
