package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.ItemUnavailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.DateUtils;
import ru.practicum.shareit.utils.InvalidObjectException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemValidator itemValidator;
    private final ItemDtoValidator itemDtoValidator;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto get(int id) {
        return itemMapper.toDto(itemRepository.findById(id).orElseThrow());
    }

    @Override
    public ItemDto get(int id, int userId) {
        var dto = get(id);
        return addBookings(dto, userId);
    }

    @Override
    public List<ItemDto> getAll(int userId, Pageable pageable) {
        return itemRepository.findByUserId(userId, pageable).stream().map(itemMapper::toDto).map(x -> addBookings(x, userId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getForRequest(int requestId) {
        return toDto(itemRepository.findByRequestId(requestId, Pageable.unpaged()));
    }

    @Override
    public int add(ItemDto item) {
        userService.get(item.getUserId());

        Item i = itemMapper.toModel(item);
        itemValidator.validate(i);
        return itemRepository.save(i).getId();
    }

    @Override
    public void update(ItemDto item) {
        itemDtoValidator.validateForUpdate(item);

        var existing = itemRepository
                .findById(item.getId())
                .orElseThrow();

        if (item.getUserId() != null && existing.getUserId() != item.getUserId()) {
            throw new InvalidObjectException("Item " + item.getId() + " belongs to other user");
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

        itemRepository.save(existing);
    }

    @Override
    public void delete(int id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        } else {
            var list1 = itemRepository.findByNameContainingIgnoreCaseAndAvailable(text, true, pageable).stream();
            var list2 = itemRepository.findByDescriptionContainingIgnoreCaseAndAvailable(text, true, pageable).stream();
            return Stream.concat(list1, list2).distinct().map(itemMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    public int addComment(int userId, int itemId, CommentDto comment) {
        // проверим что предмет существует
        get(itemId);

        var bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId, Pageable.unpaged());
        var now = DateUtils.now();
        if (bookings.stream().noneMatch(b -> b.getStatus() == BookingStatus.APPROVED && b.getStartDate().isBefore(now))) {
            // можно оставлять комментарии только для арендованных предметов
            throw new ItemUnavailableException(itemId);
        }

        var c = commentMapper.toModel(comment, itemId, userId, now);
        return commentRepository.save(c).getId();
    }

    @Override
    public CommentDto findComment(int id) {
        return commentMapper.toDto(commentRepository.findById(id).orElseThrow());
    }

    private ItemDto addBookings(ItemDto dto, int userId) {
        if (userId == dto.getUserId()) {
            var builder = dto.toBuilder();
            var now = DateUtils.now();
            var lastBooking = bookingRepository.findFirstByItemIdAndEndDateBeforeOrderByStartDateDesc(dto.getId(), now);
            var nextBooking = bookingRepository.findFirstByItemIdAndEndDateAfterOrderByStartDateAsc(dto.getId(), now);

            if (lastBooking != null) {
                builder.lastBooking(BookingMapper.toShortDto(lastBooking));
            }

            if (nextBooking != null) {
                builder.nextBooking(BookingMapper.toShortDto(nextBooking));
            }
            return builder.build();
        }

        return dto;
    }

    private List<ItemDto> toDto(Page<Item> items) {
        return items.map(itemMapper::toDto).stream().collect(Collectors.toList());
    }
}
