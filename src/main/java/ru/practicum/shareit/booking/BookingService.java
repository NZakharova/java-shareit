package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.ObjectNotFoundException;
import ru.practicum.shareit.utils.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;
    private final UserRepository userRepository;
    private final BookingValidator bookingValidator;

    public int create(int bookerId, CreateBookingRequest dto) {
        bookingValidator.validate(dto);

        if (userRepository.findById(bookerId).isEmpty()) {
            throw new ObjectNotFoundException(bookerId);
        }

        if (itemRepository.findById(dto.getItemId()).orElseThrow().getUserId() == bookerId) {
            // нельзя забронировать свой предмет
            throw new ObjectNotFoundException(dto.getItemId());
        }

        var booking = mapper.toModel(dto, bookerId);
        bookingRepository.save(booking);
        return booking.getId();
    }

    public BookingDto find(int bookerId, int id) {
        var booking = find(id);
        // бронь доступна только владельцу или бронируещему
        if (booking.getBooker().getId() != bookerId && booking.getItem().getUserId() != bookerId) {
            throw new ObjectNotFoundException(bookerId);
        }
        return booking;
    }

    public BookingDto find(int id) {
        var booking = bookingRepository.findById(id).orElseThrow();
        return mapper.toDto(booking);
    }

    public List<BookingDto> find(int bookerId, BookingSearchKind searchKind) {
        if (userRepository.findById(bookerId).isEmpty()) {
            throw new ObjectNotFoundException(bookerId);
        }

        switch (searchKind) {
            case ALL:
                return toDto(bookingRepository.findByBookerIdOrderByStartDateDesc(bookerId));
            case CURRENT:
                var now = LocalDateTime.now();
                return toDto(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId, now, now));
            case PAST:
                return toDto(bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId, LocalDateTime.now()));
            case FUTURE:
                return toDto(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, LocalDateTime.now()));
            case WAITING:
                return toDto(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING));
            case REJECTED:
                return toDto(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED));
            default:
                throw new UnsupportedOperationException("Неизвестный критерий поиска: " + searchKind);
        }
    }

    public List<BookingDto> findOwned(int ownerId, BookingSearchKind searchKind) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new ObjectNotFoundException(ownerId);
        }

        switch (searchKind) {
            case CURRENT:
                return toDto(bookingRepository.findCurrentByOwnerIdOrderByStartDateDesc(ownerId, LocalDateTime.now()));
            case PAST:
                return toDto(bookingRepository.findPastByOwnerIdOrderByStartDateDesc(ownerId, LocalDateTime.now()));
            case FUTURE:
                return toDto(bookingRepository.findFutureByOwnerIdOrderByStartDateDesc(ownerId, LocalDateTime.now()));
            case ALL:
                return toDto(bookingRepository.findByOwnerIdOrderByStartDateDesc(ownerId));
            case WAITING:
                return toDto(bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING));
            case REJECTED:
                return toDto(bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED));
            default:
                throw new UnsupportedOperationException("Not implemented");
        }

    }

    public void setApproved(int bookerId, int bookingId, boolean approved) {
        var booking = bookingRepository.findById(bookingId).orElseThrow();
        var item = itemRepository.findById(booking.getItemId()).orElseThrow();
        if (item.getUserId() != bookerId) {
            throw new ObjectNotFoundException(bookerId);
        }

        if (!isValidTransition(booking.getStatus(), approved ? BookingStatus.APPROVED : BookingStatus.REJECTED)) {
            throw new ValidationException("Нельзя изменить статус после подтверждения брони");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
    }

    private static boolean isValidTransition(BookingStatus from, BookingStatus to) {
        switch (from) {
            case WAITING:
                return to == BookingStatus.APPROVED || to == BookingStatus.REJECTED;
            case APPROVED:
                return to == BookingStatus.REJECTED;
            case REJECTED:
            default:
                return false;
        }
    }

    private List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
