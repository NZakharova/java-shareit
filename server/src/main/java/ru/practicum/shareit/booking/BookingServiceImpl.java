package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.DateUtils;
import ru.practicum.shareit.utils.ObjectNotFoundException;
import ru.practicum.shareit.utils.ValidationException;

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
    @Transactional
    public int create(int bookerId, CreateBookingRequest dto) {
        bookingValidator.validate(dto);
        if (!Boolean.TRUE.equals(itemService.get(dto.getItemId()).getAvailable())) {
            throw new ItemUnavailableException(dto.getItemId());
        }

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
    @Transactional(readOnly = true)
    public BookingDto get(int bookerId, int id) {
        var booking = get(id);
        // бронь доступна только владельцу или бронируещему
        if (booking.getBooker().getId() != bookerId && booking.getItem().getUserId() != bookerId) {
            throw new ObjectNotFoundException(id, "booking");
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(int id) {
        var booking = bookingRepository.findById(id).orElseThrow();
        var item = itemService.get(booking.getItemId());
        var booker = userService.get(booking.getBookerId());

        return mapper.toDto(booking, item, booker);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAll(int bookerId, BookingSearchKind searchKind, Pageable pageable) {
        userService.get(bookerId);

        switch (searchKind) {
            case ALL:
                return toDto(bookingRepository.findByBookerIdOrderByStartDateDesc(bookerId, pageable));
            case CURRENT:
                var now = DateUtils.now();
                return toDto(bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId, now, now, pageable));
            case PAST:
                return toDto(bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId, DateUtils.now(), pageable));
            case FUTURE:
                return toDto(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, DateUtils.now(), pageable));
            case WAITING:
                return toDto(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return toDto(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED, pageable));
            default:
                throw new UnsupportedOperationException("Unknown search kind: " + searchKind);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllOwned(int ownerId, BookingSearchKind searchKind, Pageable pageable) {
        userService.get(ownerId);

        switch (searchKind) {
            case CURRENT:
                return toDto(bookingRepository.findCurrentByOwnerIdOrderByStartDateDesc(ownerId, DateUtils.now(), pageable));
            case PAST:
                return toDto(bookingRepository.findPastByOwnerIdOrderByStartDateDesc(ownerId, DateUtils.now(), pageable));
            case FUTURE:
                return toDto(bookingRepository.findFutureByOwnerIdOrderByStartDateDesc(ownerId, DateUtils.now(), pageable));
            case ALL:
                return toDto(bookingRepository.findByOwnerIdOrderByStartDateDesc(ownerId, pageable));
            case WAITING:
                return toDto(bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return toDto(bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED, pageable));
            default:
                throw new UnsupportedOperationException("Unknown search kind: " + searchKind);
        }
    }

    @Override
    @Transactional
    public void setApproved(int bookerId, int bookingId, boolean approved) {
        var booking = bookingRepository.findById(bookingId).orElseThrow();
        var item = itemService.get(booking.getItemId());
        if (item.getUserId() != bookerId) {
            throw new ObjectNotFoundException(bookerId, "item");
        }

        var statusToSet = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (!isValidTransition(booking.getStatus(), statusToSet)) {
            throw new ValidationException("Cannot change status after booking was approved or rejected");
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
