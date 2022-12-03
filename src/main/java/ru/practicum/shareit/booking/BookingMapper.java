package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public BookingDto toDto(Booking booking) {
        var item = itemRepository.findById(booking.getItemId()).orElseThrow();
        var booker = userRepository.findById(booking.getBookerId()).orElseThrow();
        return new BookingDto(booking.getId(),
                booking.getBookerId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                itemMapper.toDto(item),
                UserMapper.toDto(booker)
        );
    }

    public static ShortBookingDto toShortDto(Booking booking) {
        return new ShortBookingDto(booking.getId(), booking.getBookerId());
    }

    public Booking toModel(CreateBookingRequest request, Integer requesterId) {
        return new Booking(
                0, request.getItemId(), BookingStatus.WAITING, requesterId, request.getStart(), request.getEnd()
        );
    }
}
