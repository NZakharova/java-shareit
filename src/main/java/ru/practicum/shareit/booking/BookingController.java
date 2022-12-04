package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/bookings")
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") int bookerId, @RequestBody CreateBookingRequest dto) {
        log.info("Создание брони пользователем " + bookerId + ": " + dto);
        int id = bookingService.create(bookerId, dto);
        log.info("Создана бронь " + id);
        return bookingService.find(bookerId, id);
    }

    @GetMapping("/bookings/owner")
    public List<BookingDto> findMineItems(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestParam Optional<String> state) {
        log.info("Запрос брони пользователя " + ownerId + " со статусом " + state.orElse("<null>"));
        return bookingService.findOwned(ownerId, parseSearchKind(state));
    }

    @GetMapping("/bookings")
    public List<BookingDto> findByState(@RequestHeader("X-Sharer-User-Id") int bookerId, @RequestParam Optional<String> state) {
        log.info("Запрос брони пользователем " + bookerId + " со статусом " + state.orElse("<null>"));
        return bookingService.find(bookerId, parseSearchKind(state));
    }

    @GetMapping("/bookings/{bookingId}")
    public BookingDto find(@RequestHeader("X-Sharer-User-Id") int bookerId, @PathVariable int bookingId) {
        log.info("Запрос брони " + bookingId + " пользователем " + bookerId);
        return bookingService.find(bookerId, bookingId);
    }

    @PatchMapping("/bookings/{bookingId}")
    public BookingDto setApproved(@RequestHeader("X-Sharer-User-Id") int bookerId, @PathVariable int bookingId, @RequestParam boolean approved) {
        log.info("Изменяется статус брони " + bookingId + " пользователем " + bookerId + ": " + approved);
        bookingService.setApproved(bookerId, bookingId, approved);
        return bookingService.find(bookingId);
    }

    private static BookingSearchKind parseSearchKind(Optional<String> state) {
        if (state.isEmpty()) {
            return BookingSearchKind.ALL;
        }

        try {
            return BookingSearchKind.valueOf(state.get());
        } catch (Exception t) {
            throw new UnsupportedOperationException("Unknown state: " + state.get());
        }
    }
}
