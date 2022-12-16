package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.ObjectNotFoundException;
import ru.practicum.shareit.utils.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final BookingValidator bookingValidator;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public int create(int bookerId, CreateBookingRequest dto) {
        bookingValidator.validate(dto);

        userService.get(bookerId);
        if (itemService.get(dto.getItemId()).getUserId() == bookerId) {
            // нельзя забронировать свой предмет
            throw new ObjectNotFoundException(dto.getItemId(), "item");
        }

        var booking = mapper.toModel(dto, bookerId);
        bookingRepository.save(booking);
        return booking.getId();
    }

    @Override
    public BookingDto get(int bookerId, int id) {
        var booking = get(id);
        // бронь доступна только владельцу или бронируещему
        if (booking.getBooker().getId() != bookerId && booking.getItem().getUserId() != bookerId) {
            throw new ObjectNotFoundException(id, "booking");
        }
        return booking;
    }

    @Override
    public BookingDto get(int id) {
        var booking = bookingRepository.findById(id).orElseThrow();
        var item = itemService.get(booking.getItemId());
        var booker = userService.get(booking.getBookerId());

        return mapper.toDto(booking, item, booker);
    }

    @Override
    public List<BookingDto> getAll(int bookerId, BookingSearchKind searchKind, Pageable pageable) {
        userService.get(bookerId);

        switch (searchKind) {
            case ALL:
                return toDto(bookingRepository.findByBookerIdOrderByStartDateDesc(bookerId, pageable));
            case CURRENT:
                var now = LocalDateTime.now();
                return toDto(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId, now, now, pageable));
            case PAST:
                return toDto(bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId, LocalDateTime.now(), pageable));
            case FUTURE:
                return toDto(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, LocalDateTime.now(), pageable));
            case WAITING:
                return toDto(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return toDto(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED, pageable));
            default:
                throw new UnsupportedOperationException("Неизвестный критерий поиска: " + searchKind);
        }
    }

    @Override
    public List<BookingDto> getAllOwned(int ownerId, BookingSearchKind searchKind, Pageable pageable) {
        userService.get(ownerId);

        switch (searchKind) {
            case CURRENT:
                return toDto(bookingRepository.findCurrentByOwnerIdOrderByStartDateDesc(ownerId, LocalDateTime.now(), pageable));
            case PAST:
                return toDto(bookingRepository.findPastByOwnerIdOrderByStartDateDesc(ownerId, LocalDateTime.now(), pageable));
            case FUTURE:
                return toDto(bookingRepository.findFutureByOwnerIdOrderByStartDateDesc(ownerId, LocalDateTime.now(), pageable));
            case ALL:
                return toDto(bookingRepository.findByOwnerIdOrderByStartDateDesc(ownerId, pageable));
            case WAITING:
                return toDto(bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return toDto(bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED, pageable));
            default:
                throw new UnsupportedOperationException("Not implemented");
        }
    }

    @Override
    public void setApproved(int bookerId, int bookingId, boolean approved) {
        var booking = bookingRepository.findById(bookingId).orElseThrow();
        var item = itemService.get(booking.getItemId());
        if (item.getUserId() != bookerId) {
            throw new ObjectNotFoundException(bookerId, "item");
        }

        var statusToSet = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (!isValidTransition(booking.getStatus(), statusToSet)) {
            throw new ValidationException("Нельзя изменить статус после подтверждения брони");
        }

        booking.setStatus(statusToSet);

        bookingRepository.save(booking);
    }

    private static boolean isValidTransition(BookingStatus from, BookingStatus to) {
        switch (from) {
            case WAITING:
                return to == BookingStatus.APPROVED || to == BookingStatus.REJECTED;
            case APPROVED:
                return to == BookingStatus.REJECTED;
            case REJECTED:
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }

    private List<BookingDto> toDto(Page<Booking> bookings) {
        return bookings
                .map(b -> {
                    var item = itemService.get(b.getItemId());
                    var booker = userService.get(b.getBookerId());
                    return mapper.toDto(b, item, booker);
                })
                .stream()
                .collect(Collectors.toList());
    }
}
