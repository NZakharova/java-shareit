package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.ItemUnavailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.InvalidObjectException;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemValidator itemValidator;
    private final ItemDtoValidator itemDtoValidator;
    private final ItemRepository itemStorage;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    ItemDto find(int id) {
        return itemMapper.toDto(itemStorage.findById(id).orElseThrow());
    }

    ItemDto find(int id, int userId) {
        var dto = find(id);
        return addBookings(dto, userId);
    }

    List<ItemDto> findAll() {
        return itemStorage.findAll().stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    List<ItemDto> findAll(int userId) {
        return itemStorage.findByUserId(userId).stream().map(itemMapper::toDto).map(x -> addBookings(x, userId)).collect(Collectors.toList());
    }

    int add(ItemDto item) {
        if (!userRepository.existsById(item.getUserId())) {
            throw new ObjectNotFoundException(item.getUserId(), "user");
        }

        Item i = itemMapper.toModel(item);
        itemValidator.validate(i);
        return itemStorage.save(i).getId();
    }

    void update(ItemDto item) {
        itemDtoValidator.validateForUpdate(item);

        var existing = itemStorage
                .findById(item.getId())
                .orElseThrow();

        if (item.getUserId() != null && existing.getUserId() != item.getUserId()) {
            throw new InvalidObjectException("Данный предмет принадлежит другому пользователю");
        }

        if (item.getName() != null) {
            existing.setName(item.getName());
        }

        if (item.getDescription() != null) {
            existing.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            existing.setAvailable(item.getAvailable());
        }

        itemStorage.save(existing);
    }

    void delete(int id) {
        itemStorage.deleteById(id);
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        } else {
            var list1 = itemStorage.findByNameContainingIgnoreCaseAndAvailable(text, true).stream();
            var list2 = itemStorage.findByDescriptionContainingIgnoreCaseAndAvailable(text, true).stream();
            return Stream.concat(list1, list2).distinct().map(itemMapper::toDto).collect(Collectors.toList());
        }
    }

    public int addComment(int userId, int itemId, CommentDto comment) {
        // проверим что предмет существует
        find(itemId);

        var bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        var now = LocalDateTime.now();
        if (bookings.stream().noneMatch(b -> b.getStatus() == BookingStatus.APPROVED && b.getStartDate().isBefore(now))) {
            // можно оставлять комментарии только для арендованных предметов
            throw new ItemUnavailableException();
        }

        var c = commentMapper.toModel(comment, itemId, userId, LocalDateTime.now());
        return commentRepository.save(c).getId();
    }

    public CommentDto findComment(int id) {
        return commentMapper.toDto(commentRepository.findById(id).orElseThrow());
    }

    private ItemDto addBookings(ItemDto dto, int userId) {
        var builder = dto.toBuilder();
        if (userId == dto.getUserId()) {
            var now = LocalDateTime.now();
            var lastBooking = bookingRepository.findFirstByItemIdAndEndDateBeforeOrderByStartDateDesc(dto.getId(), now);
            var nextBooking = bookingRepository.findFirstByItemIdAndEndDateAfterOrderByStartDateAsc(dto.getId(), now);

            if (lastBooking != null) {
                builder.lastBooking(BookingMapper.toShortDto(lastBooking));
            }

            if (nextBooking != null) {
                builder.nextBooking(BookingMapper.toShortDto(nextBooking));
            }
        }

        return builder.build();
    }
}
