package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ItemMapper {

    private final BookingRepository bookingRepository;

    public ItemMapper(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public ItemDto toDto(Item item) {
        var builder = ItemDto.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());

        var now = LocalDateTime.now();
        var lastBooking = bookingRepository.findFirstByItemIdAndEndDateBeforeOrderByStartDateDesc(item.getId(), now);
        var nextBooking = bookingRepository.findFirstByItemIdAndEndDateAfterOrderByStartDateAsc(item.getId(), now);

        if (lastBooking != null) {
            builder.lastBooking(BookingMapper.toShortDto(lastBooking));
        }

        if (nextBooking != null) {
            builder.nextBooking(BookingMapper.toShortDto(nextBooking));
        }

        return builder.build();
    }

    public Item toModel(ItemDto item) {
        return new Item(
                Optional.ofNullable(item.getId()).orElse(0),
                item.getUserId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
